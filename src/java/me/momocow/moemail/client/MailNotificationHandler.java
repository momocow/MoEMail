package me.momocow.moemail.client;

import me.momocow.moemail.client.gui.GuiMailNotification;
import me.momocow.moemail.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MailNotificationHandler
{
	private GuiMailNotification mn;
	
	private boolean isSoundPlayed = false;
	
	public MailNotificationHandler(int count) 
	{
		this.mn = new GuiMailNotification(Minecraft.getMinecraft(), count);
		this.mn.setHandler(this);
	}
	
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onRenderGameOverlay(RenderGameOverlayEvent event)
	{
		if(!this.isSoundPlayed)
		{
			playNotificationSound();
			this.isSoundPlayed = true;
		}
		
		this.mn.updateScreen();
	}
	
	public static void playNotificationSound(int soundId)
	{
		SoundEvent sound = (soundId >= 0 && soundId < 10)? ModSounds.sounds.get(soundId): ModSounds.sounds.get(0);
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		
		Minecraft.getMinecraft().theWorld.playSound(player.posX, player.posY, player.posZ,
				sound, SoundCategory.MASTER, 1F, 1F, false);
	}
}