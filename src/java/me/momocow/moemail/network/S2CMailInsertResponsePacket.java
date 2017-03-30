package me.momocow.moemail.network;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.client.gui.GuiSendMailConfirm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CMailInsertResponsePacket implements IMessage
{
	ResultMailInsert result = ResultMailInsert.UnknownError;
	
	public S2CMailInsertResponsePacket() {}
	
	public S2CMailInsertResponsePacket(ResultMailInsert r)
	{
		this.result = r;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.result = ResultMailInsert.get(buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.result.toInt());
	}

	public static class Handler implements IMessageHandler<S2CMailInsertResponsePacket, IMessage>
	{
		@Override
		public IMessage onMessage(S2CMailInsertResponsePacket message, MessageContext ctx) 
		{
			GuiScreen screen = Minecraft.getMinecraft().currentScreen;
			if(screen instanceof GuiSendMailConfirm)
			{
				((GuiSendMailConfirm) screen).onMailSent(message.result);
			}
			return null;
		}
	}
	
	public enum ResultMailInsert
	{
		UnknownError(-1), Success(0), ReceiverNotFound(1);
		
		private int value;
		
		private ResultMailInsert(int n)
		{
			this.value = n;
		}
		
		public int toInt()
		{
			return this.value;
		}
		
		public static ResultMailInsert get(int n)
		{
			switch(n)
			{
				case 0:
					return ResultMailInsert.Success;
				case 1:
					return ResultMailInsert.ReceiverNotFound;
				default:
					return ResultMailInsert.UnknownError;
			}
		}
	}
}
