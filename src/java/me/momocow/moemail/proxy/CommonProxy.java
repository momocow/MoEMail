package me.momocow.moemail.proxy;

import org.apache.logging.log4j.Logger;

import me.momocow.moemail.MoEMail;
import me.momocow.moemail.handler.GuiHandler;
import me.momocow.moemail.init.ModChannels;
import me.momocow.moemail.init.ModCommands;
import me.momocow.moemail.init.ModConfigs;
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
	
	public void registerGuiHandlers()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(MoEMail.instance, new GuiHandler());
		logger.info("GuiHandler is registered. ");
	}
	
	public void preInit(FMLPreInitializationEvent e) throws Exception
	{
		ModConfigs.init(e);
		ModChannels.init(e);
	}
	
	public void init(FMLInitializationEvent e) throws Exception
	{
		this.registerGuiHandlers();
	}
	
	public void postInit(FMLPostInitializationEvent e) throws Exception
	{
		MailPool.init(e);
		MoHTTPD.init(e);
		
		ModConfigs.save(e);
	}

	public void serverStarting(FMLServerStartingEvent e) throws Exception 
	{
		ModCommands.register(e);
		MoHTTPD.start(e);
	}

	public void serverStopping(FMLServerStoppingEvent e) throws Exception 
	{
		MoHTTPD.stop(e);
	}
}
