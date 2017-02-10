package me.momocow.moemail.config;

import java.io.File;

import org.apache.logging.log4j.Logger;

import me.momocow.mobasic.config.MoConfig;
import me.momocow.moemail.MoEMail;
import me.momocow.moemail.reference.Reference;
import net.minecraftforge.common.config.Configuration;

public class ConfigHttpd implements MoConfig
{
	private Configuration cfg;
	private static Logger logger = MoEMail.logger;
	
	private final String CONFIG_FILE = Reference.MOD_ID + "-httpd.cfg";
	private final String CATEGORY_HTTPD = Reference.MOD_ID + ".HTTPD";
	private final String CATEGORY_WWW = Reference.MOD_ID + ".WWW";
	
	public int defaultPort = 25566;
	public String customKeystoreURI = "";
	public String hostname = "localhost";
	
	public static class WWW
	{
		public String entrancePage = "";
		public String homePage = "";
	}
	public WWW www = new WWW();
	
	public ConfigHttpd(File configDir)
	{
		cfg = new Configuration(new File(configDir, this.CONFIG_FILE), Reference.VERSION);
	}
	
	public MoConfig load()
	{
		try
        {
			this.cfg.load();
			
			cfg.addCustomCategoryComment(CATEGORY_HTTPD, "MoEMail HTTP Daemon Configuration");
			cfg.setCategoryLanguageKey(CATEGORY_HTTPD, CATEGORY_HTTPD);
			
			defaultPort = cfg.getInt("defaultPort", CATEGORY_HTTPD, defaultPort, 1000, 65535, "Default port for the light weight web server");
			customKeystoreURI = cfg.getString("customKeystoreURI", CATEGORY_HTTPD, customKeystoreURI, "Keystore file for HTTPS. Leave blank if you do not know what it is.");
			hostname = cfg.getString("hostname", CATEGORY_HTTPD,  hostname, "Host name for Https server. Set to 'localhost' or '127.0.0.1' will make your server private.");
			
			cfg.addCustomCategoryComment(CATEGORY_WWW, "MoEMail Custom Web Configuration");
			cfg.setCategoryLanguageKey(CATEGORY_WWW, CATEGORY_WWW);
			
			this.www.entrancePage = cfg.getString("entrancePage", CATEGORY_WWW,  this.www.entrancePage, "Custom log-in page.");
			this.www.homePage = cfg.getString("homePage", CATEGORY_WWW,  this.www.homePage, "Custom home page.");
        }
        catch (Exception ex)
        {
        	logger.error("Fail to init the config!", ex);
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
