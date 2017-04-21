package me.momocow.moemail.handler;

import me.momocow.moemail.init.ModConfigs;
import me.momocow.moemail.reference.Reference;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigHandler 
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if(event.getModID() == Reference.MOD_ID)
		{
			ModConfigs.save();
			ModConfigs.load();
		}
	}
}
