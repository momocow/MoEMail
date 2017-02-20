package me.momocow.moemail.network;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.client.gui.GuiMailBoxAccount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CPasswdResultPacket implements IMessage
{
	private boolean result;
	
	public S2CPasswdResultPacket() {}
	
	public S2CPasswdResultPacket(boolean r)
	{
		this.result = r;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.result = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeBoolean(this.result);
	}

	//CLIENT
	public static class Handler implements IMessageHandler<S2CPasswdResultPacket, IMessage>
	{
		@Override
		public IMessage onMessage(S2CPasswdResultPacket message, MessageContext ctx) {
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if(gui instanceof GuiMailBoxAccount)
			{
				((GuiMailBoxAccount)gui).updatePasswdStatus(message.result);
			}
			return null;
		}
	}
}
