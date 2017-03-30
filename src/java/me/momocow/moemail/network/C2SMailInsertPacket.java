package me.momocow.moemail.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import me.momocow.mobasic.proxy.Server;
import me.momocow.moemail.network.S2CMailInsertResponsePacket.ResultMailInsert;
import me.momocow.moemail.server.MailPool;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SMailInsertPacket implements IMessage
{
	private String title = "";
	private UUID from = null;
	private String to = "";
	private String content = "";
	
	public C2SMailInsertPacket() {}
	
	public C2SMailInsertPacket(String t, UUID f, String ttl, String txt) 
	{
		this.to = t;
		this.from = f;
		this.title = ttl;
		this.content = txt;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.to = ByteBufUtils.readUTF8String(buf);
		this.from = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.title = ByteBufUtils.readUTF8String(buf);
		this.content = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.to);
		ByteBufUtils.writeUTF8String(buf, this.from.toString());
		ByteBufUtils.writeUTF8String(buf, this.title);
		ByteBufUtils.writeUTF8String(buf, this.content);
	}

	//SERVER
	public static class Handler implements IMessageHandler<C2SMailInsertPacket, S2CMailInsertResponsePacket>
	{
		@Override
		public S2CMailInsertResponsePacket onMessage(C2SMailInsertPacket message, MessageContext ctx) 
		{
			ResultMailInsert res = ResultMailInsert.Success;
			UUID receiver = Server.getPlayerId(message.to);
			
			if(receiver == null)
			{
				res = ResultMailInsert.ReceiverNotFound;
			}
			
			if(res == ResultMailInsert.Success)
			{
				MailPool.instance().send(receiver, message.from, Server.getPlayerName(message.from), message.title, message.content);
			}
			
			return new S2CMailInsertResponsePacket(res);
		}
	}
}
