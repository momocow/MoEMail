package me.momocow.moemail.network;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.server.MoHTTPD;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SFetchMailBoxURLPacket implements IMessage
{
	public C2SFetchMailBoxURLPacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {	}

	@Override
	public void toBytes(ByteBuf buf) {	}

	//SERVER
	public static class Handler implements IMessageHandler<C2SFetchMailBoxURLPacket, S2CMailBoxURLPacket>
	{
		@Override
		public S2CMailBoxURLPacket onMessage(C2SFetchMailBoxURLPacket message, MessageContext ctx) 
		{
			String url = null;
			if(MoHTTPD.instance() != null)
			{
				synchronized(MoHTTPD.instance())
				{
					url = MoHTTPD.instance().getURL();
				}
			}
			return new S2CMailBoxURLPacket(url);
		}
	}
}
