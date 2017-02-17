package me.momocow.moemail.network;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.client.gui.GuiMailBox;
import me.momocow.moemail.server.MailPool.Mail.Header;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CMailHeaderPacket implements IMessage
{
	private int mailCount;
	private List<Header> headers = new ArrayList<Header>();
	
	public S2CMailHeaderPacket() {}

	public S2CMailHeaderPacket(int c, List<Header> h)
	{
		this.mailCount = c;
		this.headers = h;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.mailCount = buf.readInt();
		
		this.headers.clear();
		int count = buf.readInt();
		for(int i = 0; i< count; i++)
		{
			this.headers.add(Header.fromNBT(ByteBufUtils.readTag(buf)));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.mailCount);
		
		buf.writeInt(this.headers.size());
		for(Header head: this.headers)
		{
			ByteBufUtils.writeTag(buf, head.toNBT());
		}
	}

	//CLIENT
	public static class Handler implements IMessageHandler<S2CMailHeaderPacket, IMessage>
	{
		@Override
		public IMessage onMessage(final S2CMailHeaderPacket message, MessageContext ctx) 
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() 
				{
					@Override
					public void run() {
						GuiScreen gui = Minecraft.getMinecraft().currentScreen;
						if(gui instanceof GuiMailBox)
						{
							((GuiMailBox)gui).updatePage(message.mailCount, message.headers);
						}
					}
				}
			);
			return null;
		}
	}
}
