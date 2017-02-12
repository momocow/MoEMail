package me.momocow.moemail.init;

import me.momocow.moemail.network.C2SFetchMailPacket;
import me.momocow.moemail.reference.ID;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModChannels 
{
	public static SimpleNetworkWrapper mailSyncChannel;
	
	public static void init(FMLPreInitializationEvent e) 
	{
		mailSyncChannel = NetworkRegistry.INSTANCE.newSimpleChannel(ID.Channel.mailSync);
		mailSyncChannel.registerMessage(C2SFetchMailPacket.Handler.class, C2SFetchMailPacket.class, ID.Packet.C2SFetchMailPacket, Side.SERVER);
	}
}
