package me.momocow.moemail.network;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.client.gui.GuiDeleteMailConfirm;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CMailDeleteResponsePacket implements IMessage
{
	public S2CMailDeleteResponsePacket(){}
	
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	//CLIENT
	public static class Handler implements IMessageHandler<S2CMailDeleteResponsePacket, IMessage>
	{
		@Override
		public IMessage onMessage(S2CMailDeleteResponsePacket message, MessageContext ctx) 
		{
			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if(gui instanceof GuiDeleteMailConfirm)
			{
				((GuiDeleteMailConfirm)gui).onMailDeleted();
			}
			return null;
		}
		
	}
}
