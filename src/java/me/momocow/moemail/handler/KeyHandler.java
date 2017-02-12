package me.momocow.moemail.handler;

import me.momocow.moemail.MoEMail;
import me.momocow.moemail.init.ModKeyBindings;
import me.momocow.moemail.reference.ID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class KeyHandler 
{
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
	public void onKeyInput(KeyInputEvent event)
	{
		if(ModKeyBindings.key_M.isPressed())
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			player.openGui(MoEMail.instance, ID.Gui.mailBox, Minecraft.getMinecraft().theWorld, (int)player.posX, (int)player.posY, (int)player.posZ);
		}
	}
}
