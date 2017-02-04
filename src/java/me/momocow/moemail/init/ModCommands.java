package me.momocow.moemail.init;

import org.apache.logging.log4j.Logger;

import me.momocow.moemail.MoEMail;
import me.momocow.moemail.command.CommandSendMail;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ModCommands 
{
	private static Logger logger = MoEMail.logger;
	
	public static void register(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandSendMail());
		
		logger.info("Commands registered.");
	}
}
