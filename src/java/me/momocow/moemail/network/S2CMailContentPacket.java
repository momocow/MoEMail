package me.momocow.moemail.network;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.client.gui.GuiMail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CMailContentPacket implements IMessage
{
	private String content;
	
	public S2CMailContentPacket() {};
	
	public S2CMailContentPacket(String c)
	{
		this.content = c;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.content = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.content);
	}

	//CLIENT
	public static class Handler implements IMessageHandler<S2CMailContentPacket, IMessage>
	{
		@Override
		public IMessage onMessage(final S2CMailContentPacket message, MessageContext ctx) 
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() 
				{
					@Override
					public void run() {
						GuiScreen gui = Minecraft.getMinecraft().currentScreen;
						if(gui instanceof GuiMail)
						{
							((GuiMail)gui).updateContent(message.content);
						}
					}
				}
			);
			return null;
		}
	}
}
