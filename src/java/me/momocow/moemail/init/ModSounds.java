package me.momocow.moemail.init;

import java.util.HashMap;
import java.util.Map;

import me.momocow.moemail.reference.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModSounds 
{
	public static Map<Integer, SoundEvent> sounds = new HashMap<Integer, SoundEvent>();
	public static SoundEvent MAIL_NOTIFICATION_0;
	public static SoundEvent MAIL_NOTIFICATION_1;
	public static SoundEvent MAIL_NOTIFICATION_2;
	public static SoundEvent MAIL_NOTIFICATION_3;
	public static SoundEvent MAIL_NOTIFICATION_4;
	public static SoundEvent MAIL_NOTIFICATION_5;
	public static SoundEvent MAIL_NOTIFICATION_6;
	public static SoundEvent MAIL_NOTIFICATION_7;
	public static SoundEvent MAIL_NOTIFICATION_8;
	public static SoundEvent MAIL_NOTIFICATION_9;
	
	public static void init(FMLPreInitializationEvent e)
	{
		MAIL_NOTIFICATION_0 = initSound("mail_notification_0");
		MAIL_NOTIFICATION_1 = initSound("mail_notification_1");
		MAIL_NOTIFICATION_2 = initSound("mail_notification_2");
		MAIL_NOTIFICATION_3 = initSound("mail_notification_3");
		MAIL_NOTIFICATION_4 = initSound("mail_notification_4");
		MAIL_NOTIFICATION_5 = initSound("mail_notification_5");
		MAIL_NOTIFICATION_6 = initSound("mail_notification_6");
		MAIL_NOTIFICATION_7 = initSound("mail_notification_7");
		MAIL_NOTIFICATION_8 = initSound("mail_notification_8");
		MAIL_NOTIFICATION_9 = initSound("mail_notification_9");
	}
	
	private static SoundEvent initSound(String name)
	{
		ResourceLocation res = new ResourceLocation(Reference.MOD_ID, name);
		SoundEvent se = new SoundEvent(res);
//		GameRegistry.register(se, name);
		SoundEvent.REGISTRY.register(0, res, se);
		sounds.put(sounds.size(), se);
		
		return se;
	}
}
