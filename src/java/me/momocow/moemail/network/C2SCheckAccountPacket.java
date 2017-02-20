package me.momocow.moemail.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.server.MoHTTPD;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SCheckAccountPacket implements IMessage
{
	UUID uid;
	
	public C2SCheckAccountPacket() {}
	
	public C2SCheckAccountPacket(UUID u)
	{
		this.uid = u;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.uid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.uid.toString());
	}

	//SERVER
	public static class Handler implements IMessageHandler<C2SCheckAccountPacket, S2CAccountResultPacket>
	{
		@Override
		public S2CAccountResultPacket onMessage(C2SCheckAccountPacket message, MessageContext ctx) {
			boolean accountInitialized = false;
			if(MoHTTPD.instance() != null)
			{
				synchronized(MoHTTPD.instance())
				{
					accountInitialized = MoHTTPD.instance().hasUser(message.uid);
				}
			}
			return new S2CAccountResultPacket(accountInitialized);
		}
	}
}
