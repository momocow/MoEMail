package me.momocow.moemail.network;

import io.netty.buffer.ByteBuf;
import me.momocow.mobasic.proxy.Server;
import me.momocow.moemail.client.gui.GuiMailBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CMailBoxPartialDataPacket implements IMessage
{

	@Override
	public void fromBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	//CLIENT
	public static class Handler implements IMessageHandler<S2CMailBoxPartialDataPacket, IMessage>
	{
		@Override
		public IMessage onMessage(S2CMailBoxPartialDataPacket message, MessageContext ctx) {
			Server.getServer().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					GuiScreen gui = Minecraft.getMinecraft().currentScreen;
					if(gui instanceof GuiMailBox)
					{
						GuiMailBox gmb = (GuiMailBox)gui;
					}
				}
			});
			return null;
		}
	}
}
