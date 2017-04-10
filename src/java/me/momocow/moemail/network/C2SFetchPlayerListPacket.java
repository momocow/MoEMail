package me.momocow.moemail.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import me.momocow.mobasic.proxy.Server;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SFetchPlayerListPacket implements IMessage
{
	UUID sender;
	
	public C2SFetchPlayerListPacket() {}
	
	public C2SFetchPlayerListPacket(UUID from) 
	{
		this.sender = from;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.sender = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, this.sender.toString());
	}

	//SERVER
	public static class Handler implements IMessageHandler<C2SFetchPlayerListPacket, S2CPlayerListPacket>
	{
		@Override
		public S2CPlayerListPacket onMessage(C2SFetchPlayerListPacket message, MessageContext ctx) 
		{
			return new S2CPlayerListPacket(Server.getProfile());
		}
	}
}
