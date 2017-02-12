package me.momocow.moemail.init;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import me.momocow.moemail.MoEMail;
import me.momocow.moemail.handler.KeyHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ModKeyBindings 
{
	private static Logger logger = MoEMail.logger;
	
	public static KeyBinding key_M;
	
	public static void init(FMLInitializationEvent e)
	{
		key_M = new KeyBinding("key.moemail.mailBoxGui.desc", Keyboard.KEY_M, "key.moemail.category.name");
		logger.info("KeyBings are initialized. ");
		
		MinecraftForge.EVENT_BUS.register(new KeyHandler());
		logger.info("KeyHandler is registered. ");
	}
}
