package me.momocow.moemail.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.server.MailPool;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SFetchMailPacket implements IMessage
{
	private UUID uid;
	
	public C2SFetchMailPacket() {}
	
	public C2SFetchMailPacket(UUID usr)
	{
		this.uid = usr;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.uid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.uid.toString());
	}
	
	//SERVER
	public static class Handler implements IMessageHandler<C2SFetchMailPacket, S2CMailBoxPartialDataPacket>
	{
		@Override
		public S2CMailBoxPartialDataPacket onMessage(C2SFetchMailPacket message, MessageContext ctx) 
		{
//			MailPool.instance()
			return new S2CMailBoxPartialDataPacket();
		}
	}
}
