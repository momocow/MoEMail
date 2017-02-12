package me.momocow.moemail.proxy;

import org.apache.logging.log4j.Logger;

import me.momocow.moemail.MoEMail;
import me.momocow.moemail.handler.GuiHandler;
import me.momocow.moemail.server.MailPool;
import me.momocow.moemail.server.MoHTTPD;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public abstract class CommonProxy 
{
	private static Logger logger = MoEMail.logger;
	
	public void registerGuiHandler()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(MoEMail.instance, new GuiHandler());
		logger.info("GuiHandler is registered. ");
	}
	
	public void preInit(FMLPreInitializationEvent e) throws Exception
	{}
	
	public void init(FMLInitializationEvent e) throws Exception
	{}
	
	public void postInit(FMLPostInitializationEvent e) throws Exception
	{
		MailPool.init(e);
		MoHTTPD.init(e);
	}

	public void serverStarting(FMLServerStartingEvent e) throws Exception 
	{
		MoHTTPD.start(e);
	}

	public void serverStopping(FMLServerStoppingEvent e) throws Exception 
	{
		MoHTTPD.stop(e);
	}
}
