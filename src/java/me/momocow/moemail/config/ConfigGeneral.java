package me.momocow.moemail.config;

import java.io.File;

import org.apache.logging.log4j.Logger;

import me.momocow.mobasic.config.MoConfig;
import me.momocow.moemail.MoEMail;
import me.momocow.moemail.reference.Reference;
import net.minecraftforge.common.config.Configuration;

public class ConfigGeneral implements MoConfig 
{
	private Configuration cfg;
	private static Logger logger = MoEMail.logger;
	
	private final String CONFIG_FILE = Reference.MOD_ID + "-general.cfg";
	private final String CATEGORY_LOGS = Reference.MOD_ID + ".Logs";
	private final String CATEGORY_HTTPD = Reference.MOD_ID + ".Httpd";
	
	public String mailStorageDir = "data" + File.separator;
	
	public static class Httpd
	{
		public boolean isDedicatedServerOnly = true;
	}
	public Httpd httpd = new Httpd();
	
	public ConfigGeneral(File configDir)
	{
		cfg = new Configuration(new File(configDir.getPath(), this.CONFIG_FILE), Reference.VERSION);
	}
	
	public MoConfig load()
	{
		try
        {
			this.cfg.load();
			
			cfg.addCustomCategoryComment(CATEGORY_LOGS, "MoEMail Data Storage Configuration");
			cfg.setCategoryLanguageKey(CATEGORY_LOGS, CATEGORY_LOGS);
			mailStorageDir = cfg.getString("mailStorageDir", CATEGORY_LOGS, mailStorageDir, 
					"A relative path from the Minecraft directory to the logs directory where you allow the mod to place the mail pool storage. The mod will automatically create its own directory under this provided directory.");
			
			cfg.addCustomCategoryComment(CATEGORY_HTTPD, "MoEMail Http Server Configuration");
			cfg.setCategoryLanguageKey(CATEGORY_HTTPD, CATEGORY_HTTPD);
			this.httpd.isDedicatedServerOnly = cfg.getBoolean("isDedicatedServerOnly", CATEGORY_HTTPD, this.httpd.isDedicatedServerOnly, "Leave it as default value if you do not know what it is.");
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
}
