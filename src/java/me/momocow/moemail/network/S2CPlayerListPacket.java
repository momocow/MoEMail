package me.momocow.moemail.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import me.momocow.moemail.client.gui.GuiNewMail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CPlayerListPacket implements IMessage
{
	private List<GameProfile> playerlist = new ArrayList<GameProfile>();
	
	public S2CPlayerListPacket() {}
	
	public S2CPlayerListPacket(List<GameProfile> players) 
	{
		this.playerlist = players;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.playerlist.clear();
		int count = buf.readInt();
		for(int i = 0; i< count; i++)
		{
			UUID id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
			String name = ByteBufUtils.readUTF8String(buf);
			this.playerlist.add(new GameProfile(id, name));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.playerlist.size());
		for(GameProfile profile: this.playerlist)
		{
			ByteBufUtils.writeUTF8String(buf, profile.getId().toString());
			ByteBufUtils.writeUTF8String(buf, profile.getName());
		}
	}

	//CLIENT
	public static class Handler implements IMessageHandler<S2CPlayerListPacket, IMessage>
	{
		@Override
		public IMessage onMessage(S2CPlayerListPacket message, MessageContext ctx) 
		{
			GuiScreen screen = Minecraft.getMinecraft().currentScreen;
			
			if(screen instanceof GuiNewMail)
			{
				((GuiNewMail) screen).setCandidateReceivers(message.playerlist);
			}
			
			return null;
		}
	}
}
