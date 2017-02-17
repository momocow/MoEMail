package me.momocow.moemail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.momocow.moemail.proxy.CommonProxy;
import me.momocow.moemail.reference.Reference;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public class MoEMail
{
	@Mod.Instance(Reference.MOD_ID)
	public static MoEMail instance;
	
	@SidedProxy(clientSide = Reference.CLIENTPROXY, serverSide=Reference.SERVERPROXY)
	public static CommonProxy proxy;
		
	public static Logger logger = LogManager.getLogger(Reference.MOD_ID);
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) throws Exception
	{
		logger.info("<PRE INIT>");
		
		proxy.preInit(e);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e) throws Exception
	{
		logger.info("<INIT>");
		
		proxy.init(e);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) throws Exception
	{
		logger.info("<POST INIT>");
		
		proxy.postInit(e);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent e) throws Exception
	{
		logger.info("Server is starting...");
		
		proxy.serverStarting(e);
	}
	
	@EventHandler
	public void serverStopping(FMLServerStoppingEvent e) throws Exception
	{
		logger.info("Server is stopping...");
		
		proxy.serverStopping(e);
	}
}
