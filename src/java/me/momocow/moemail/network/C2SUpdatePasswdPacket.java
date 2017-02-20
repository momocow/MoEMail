package me.momocow.moemail.network;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.server.MoHTTPD;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SUpdatePasswdPacket implements IMessage
{
	private UUID uid;
	private String oldPasswd;
	private String newPasswd;
	
	public C2SUpdatePasswdPacket() {}
	
	public C2SUpdatePasswdPacket(UUID user, String oldPwd, String newPwd)
	{
		this.uid = user;
		this.oldPasswd = oldPwd;
		this.newPasswd = newPwd;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.uid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
		this.oldPasswd = ByteBufUtils.readUTF8String(buf);
		this.newPasswd = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, this.uid.toString());
		ByteBufUtils.writeUTF8String(buf, this.oldPasswd);
		ByteBufUtils.writeUTF8String(buf, this.newPasswd);
	}
	
	//SERVER
	public static class Handler implements IMessageHandler<C2SUpdatePasswdPacket, S2CPasswdResultPacket>
	{
		@Override
		public S2CPasswdResultPacket onMessage(C2SUpdatePasswdPacket message, MessageContext ctx) 
		{
			boolean result = false;	//fail
			
			synchronized(MoHTTPD.instance())
			{
				if(MoHTTPD.instance().hasUser(message.uid))
				{
					if(MoHTTPD.instance().authenticate(message.uid, message.oldPasswd))
					{
						MoHTTPD.instance().registerUser(message.uid, message.newPasswd);
						result = true;
					}
				}
				else
				{
					MoHTTPD.instance().registerUser(message.uid, message.newPasswd);
					result = true;
				}
			}
			
			return new S2CPasswdResultPacket(result);
		}
	}
}
