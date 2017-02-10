package me.momocow.moemail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.momocow.moemail.init.ModCommands;
import me.momocow.moemail.init.ModConfigs;
import me.momocow.moemail.reference.Reference;
import me.momocow.moemail.server.MailPool;
import me.momocow.moemail.server.MoHTTPD;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public class MoEMail
{
	@Mod.Instance(Reference.MOD_ID)
	public static MoEMail instance;
		
	public static Logger logger = LogManager.getLogger(Reference.MOD_ID);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		ModConfigs.init(e);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) throws Exception
	{
		ModConfigs.save(e);
		MailPool.init(e);
		MoHTTPD.init(e);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		logger.info("Server is starting...");
		
		ModCommands.register(e);
		
		MoHTTPD.start(e);
	}
	
	@EventHandler
	public void serverStopping(FMLServerStoppingEvent e)
	{
		MoHTTPD.stop(e);
	}
}
