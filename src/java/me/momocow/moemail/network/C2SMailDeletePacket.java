package me.momocow.moemail.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.server.MailPool;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SMailDeletePacket implements IMessage
{
	private UUID mid;
	private UUID uid;

	public C2SMailDeletePacket(){}
	
	public C2SMailDeletePacket(UUID player, UUID mail)
	{
		this.uid = player;
		this.mid = mail;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.uid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.mid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.uid.toString());
		ByteBufUtils.writeUTF8String(buf, this.mid.toString());
	}

	//SERVER
	public static class Handler implements IMessageHandler<C2SMailDeletePacket, S2CMailDeleteResponsePacket>
	{

		@Override
		public S2CMailDeleteResponsePacket onMessage(C2SMailDeletePacket message, MessageContext ctx) 
		{
			synchronized(MailPool.instance())
			{
				MailPool.instance().removeMail(message.uid, message.mid);
			}
			return new S2CMailDeleteResponsePacket();
		}
		
	}
}
