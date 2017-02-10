package me.momocow.moemail.init;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import me.momocow.mobasic.config.MoConfig;
import me.momocow.moemail.reference.Reference;
import me.momocow.moemail.MoEMail;
import me.momocow.moemail.config.ConfigGeneral;
import me.momocow.moemail.config.ConfigHttpd;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModConfigs 
{
	private static Logger logger = MoEMail.logger;
	private static File modConfigDir;
	
	public static ConfigGeneral general;
	public static ConfigHttpd httpd;
	
	private static List<MoConfig> configs = new ArrayList<MoConfig>();
	
	public static void init(FMLPreInitializationEvent e)
	{
		modConfigDir = new File(e.getModConfigurationDirectory(), Reference.MOD_ID);
		if(!modConfigDir.exists())
		{
			modConfigDir.mkdirs();
		}
		
		try
		{
			//Configs for both client and server
			general = (ConfigGeneral)initConfig(ConfigGeneral.class);
			
			//Configs for the dedicated server
			if(e.getSide() == Side.SERVER)
			{
				httpd = (ConfigHttpd)initConfig(ConfigHttpd.class);
			}
		}
		catch(Exception ex)
		{
			logger.warn("Error occurs when initializing configurations.", ex);
		}
	}
	
	private static MoConfig initConfig(Class<? extends MoConfig> configClazz) throws Exception
	{
		MoConfig conf = configClazz.getConstructor(File.class).newInstance(modConfigDir).load();
		configs.add(conf);
		
		return conf;
	}
	
	public static void save(FMLPostInitializationEvent e)
	{
		for(MoConfig conf: configs)
		{
			conf.save();
		}
	}
}
