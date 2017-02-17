package me.momocow.moemail.network;

import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.client.gui.GuiMailBox;
import me.momocow.moemail.server.MailPool;
import me.momocow.moemail.server.MailPool.Mail.Header;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SFetchMailHeaderPacket implements IMessage
{
	private UUID uid;
	private int page;
	
	public C2SFetchMailHeaderPacket() {}
	
	public C2SFetchMailHeaderPacket(UUID usr, int cursor)
	{
		this.uid = usr;
		this.page = cursor;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.uid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.page = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.uid.toString());
		buf.writeInt(this.page);
	}
	
	//SERVER
	public static class Handler implements IMessageHandler<C2SFetchMailHeaderPacket, S2CMailHeaderPacket>
	{
		@Override
		public S2CMailHeaderPacket onMessage(C2SFetchMailHeaderPacket message, MessageContext ctx) 
		{
			int mailCount;
			List<Header> mails;
			
			synchronized(MailPool.instance())
			{
				mailCount = MailPool.instance().getMailCount(message.uid);
				mails = MailPool.instance().getHeadersByPage(message.uid, GuiMailBox.PAGE_SIZE, message.page);
			}
			
			return new S2CMailHeaderPacket(mailCount, mails);
		}
	}
}
