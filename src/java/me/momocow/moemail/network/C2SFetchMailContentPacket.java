package me.momocow.moemail.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.server.MailPool;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SFetchMailContentPacket implements IMessage
{
	private UUID mid;
	
	public C2SFetchMailContentPacket() {}
	
	public C2SFetchMailContentPacket(UUID m)
	{
		this.mid = m;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.mid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.mid.toString());
	}

	//SERVER
	public static class Handler implements IMessageHandler<C2SFetchMailContentPacket, S2CMailContentPacket>
	{
		@Override
		public S2CMailContentPacket onMessage(C2SFetchMailContentPacket message, MessageContext ctx) 
		{
			String msg = "<#FAIL_TO_READ_MAIL>";
			
			synchronized(MailPool.instance())
			{
				msg = MailPool.instance().readMail(message.mid);
			}
			
			return new S2CMailContentPacket(msg);
		}
	}
}
