package me.momocow.moemail.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import me.momocow.mobasic.config.MoConfig;
import me.momocow.moemail.MoEMail;
import me.momocow.moemail.reference.Reference;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigGeneral implements MoConfig 
{
	private final Configuration cfg;
	private static Logger logger = MoEMail.logger;
	
	private final String CONFIG_FILE = Reference.MOD_ID + "-general.cfg";
	public final String CATEGORY_GENERAL = "General";
	private final String LANG_GENERAL = "config." + Reference.MOD_ID + ".ConfigGeneral.category.general";
	public final String CATEGORY_HTTPD = "Httpd";
	private final String LANG_HTTPD = "config." + Reference.MOD_ID + ".ConfigGeneral.category.httpd";
	
	public String mailStorageDir = "data" + File.separator;
	public int mailNotificationSound = 0;
	public int maxMsgSize = 500;
	
	public static class Httpd
	{
		public boolean isDedicatedServerOnly = true;
	}
	public Httpd httpd = new Httpd();
	
	public ConfigGeneral(File configDir)
	{
		cfg = new Configuration(new File(configDir.getPath(), this.CONFIG_FILE), Reference.VERSION, false);
	}
	
	public MoConfig load()
	{
		try
        {
			this.cfg.load();
			
			cfg.addCustomCategoryComment(CATEGORY_GENERAL, "MoEMail Data Storage Configuration");
			cfg.setCategoryLanguageKey(CATEGORY_GENERAL, LANG_GENERAL);
			mailStorageDir = cfg.getString("mailStorageDir", CATEGORY_GENERAL, mailStorageDir, 
					"A relative path from the Minecraft directory to the logs directory where you allow the mod to place the mail pool storage. The mod will automatically create its own directory under this provided directory.",
					"config." + Reference.MOD_ID + ".ConfigGeneral.entry.mailStorageDir");
			mailNotificationSound = cfg.getInt("mailNotificationSound", CATEGORY_GENERAL, mailNotificationSound, -1, 9, "-1: No notification sound; Can be set from ingame Gui", "config." + Reference.MOD_ID + ".ConfigGeneral.entry.mailNotificationSound");
			maxMsgSize = cfg.getInt("maxMsgSize", CATEGORY_GENERAL, maxMsgSize, 0, Integer.MAX_VALUE, "Max message length", "config." + Reference.MOD_ID + ".ConfigGeneral.entry.maxMsgSize");
			
			
			cfg.addCustomCategoryComment(CATEGORY_HTTPD, "MoEMail Http Server Configuration");
			cfg.setCategoryLanguageKey(CATEGORY_HTTPD, LANG_HTTPD);
			this.httpd.isDedicatedServerOnly = cfg.getBoolean("isDedicatedServerOnly", CATEGORY_HTTPD, this.httpd.isDedicatedServerOnly, "Leave it as default value if you do not know what it is.", "config." + Reference.MOD_ID + ".ConfigGeneral.entry.isDedicatedServerOnly");
        }
        catch (Exception ex)
        {
        	logger.error("Fail to init the config!" + ex);
        }
        finally 
        {
            this.save();
        }
		
		return this;
	}

	public MoConfig save()
	{
		if (this.cfg.hasChanged()) 
		{
			this.cfg.save();
		}
		
		return this;
	}
	
	public Configuration getConfig()
	{
		return this.cfg;
	}
	
	public List<IConfigElement> getCategories()
	{
		List<IConfigElement> categories = new ArrayList<IConfigElement>();
		for(String category: this.cfg.getCategoryNames())
		{
			categories.add(new ConfigElement(this.cfg.getCategory(category)));
		}
		
		return categories;
	}
	
	public String getConfigPath()
	{
		return this.cfg.getConfigFile().getAbsolutePath();
	}
}
