package me.momocow.moemail.config;

import java.io.File;

import org.apache.logging.log4j.Logger;

import me.momocow.moemail.MoEMail;
import me.momocow.moemail.reference.Reference;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class Config 
{
	private static Config instance;
	private Configuration cfg;
	private static Logger logger = MoEMail.logger;
	
	private final String CONFIG_FILE = "MoEMail.cfg";
	private final String CATEGORY_LOGS = Reference.MOD_ID + ".logs";
	private final String CATEGORY_HTTPD = Reference.MOD_ID + ".HTTPD";
	
	public static final class Logs
	{
		public static String mailStorageDir = "data" + File.separator;
	}
	
	public static final class HTTPD
	{
		public static int defaultPort = 25566;
	}
	
	protected Config(File configDir)
	{
		cfg = new Configuration(new File(configDir.getPath(), this.CONFIG_FILE), Reference.VERSION);
	}
	
	public Config load()
	{
		try
        {
			this.cfg.load();
			
			cfg.addCustomCategoryComment(CATEGORY_LOGS, "MoEMail Data Storage Configuration");
			cfg.setCategoryLanguageKey(CATEGORY_LOGS, CATEGORY_LOGS);
			
			Logs.mailStorageDir = cfg.getString("mailStorageDir", CATEGORY_LOGS, Logs.mailStorageDir, 
					"A relative path from the Minecraft directory to the logs directory where you allow the mod to place the mail pool storage. The mod will automatically create its own directory under this provided directory.");
			
			cfg.addCustomCategoryComment(CATEGORY_HTTPD, "MoEMail HTTP Daemon Configuration");
			cfg.setCategoryLanguageKey(CATEGORY_HTTPD, CATEGORY_HTTPD);
			
			HTTPD.defaultPort = cfg.getInt("defaultPort", CATEGORY_HTTPD, HTTPD.defaultPort, 1000, 65535, "Default port for the light weight web server");
			
			logger.info("Config is loaded!");
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
	
	public static Config init(FMLPreInitializationEvent e) 
	{
		return Config.instance = new Config(e.getModConfigurationDirectory()).load();
    }
	
	public void save()
	{
		if (this.cfg.hasChanged()) 
		{
			this.cfg.save();
			logger.info("Config save!");
		}
		else logger.info("Nothing changed in config!");
	}
	
	public static Config instance()
	{
		return Config.instance;
	}
}
