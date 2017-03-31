package me.momocow.moemail.proxy;

import org.apache.logging.log4j.Logger;

import me.momocow.moemail.MoEMail;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy
{
	public static Logger logger = MoEMail.logger;
	
	@Override
	public void preInit(FMLPreInitializationEvent e) throws Exception 
	{
		super.preInit(e);
	}
}
