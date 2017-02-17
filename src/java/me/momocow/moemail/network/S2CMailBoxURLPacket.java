package me.momocow.moemail.network;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.client.gui.GuiMailBoxAccount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CMailBoxURLPacket implements IMessage
{
	String url;
	
	public S2CMailBoxURLPacket() {}
	
	public S2CMailBoxURLPacket(String u)
	{
		this.url = u;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.url = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.url);
	}

	//CLIENT
	public static class Handler implements IMessageHandler<S2CMailBoxURLPacket, IMessage>
	{
		@Override
		public IMessage onMessage(S2CMailBoxURLPacket message, MessageContext ctx) 
		{
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if(gui instanceof GuiMailBoxAccount)
			{
				((GuiMailBoxAccount)gui).updateWebLink(message.url);
			}
			return null;
		}
	}
}
