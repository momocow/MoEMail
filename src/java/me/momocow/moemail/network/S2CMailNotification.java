package me.momocow.moemail.network;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.client.gui.GuiMailNotification.MailNotificationHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CMailNotification implements IMessage
{
	int mailCount = 0;

	public S2CMailNotification() {}
	
	public S2CMailNotification(int count)
	{
		this.mailCount = count;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.mailCount = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.mailCount);
	}

	//CLIENT
	public static class Handler implements IMessageHandler<S2CMailNotification, IMessage>
	{
		@Override
		public IMessage onMessage(S2CMailNotification message, MessageContext ctx) 
		{
			MinecraftForge.EVENT_BUS.register(new MailNotificationHandler(message.mailCount));
			return null;
		}
	}
}
