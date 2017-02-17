package me.momocow.moemail.init;

import me.momocow.moemail.network.C2SCheckAccountPacket;
import me.momocow.moemail.network.C2SFetchMailBoxURLPacket;
import me.momocow.moemail.network.C2SFetchMailContentPacket;
import me.momocow.moemail.network.C2SFetchMailHeaderPacket;
import me.momocow.moemail.network.S2CAccountResultPacket;
import me.momocow.moemail.network.S2CMailBoxURLPacket;
import me.momocow.moemail.network.S2CMailContentPacket;
import me.momocow.moemail.network.S2CMailHeaderPacket;
import me.momocow.moemail.reference.ID;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModChannels 
{
	public static SimpleNetworkWrapper mailSyncChannel;
	public static SimpleNetworkWrapper httpdChannel;
	
	public static void init(FMLPreInitializationEvent e) 
	{
		mailSyncChannel = NetworkRegistry.INSTANCE.newSimpleChannel(ID.Channel.mailSync);
		
		mailSyncChannel.registerMessage(C2SFetchMailHeaderPacket.Handler.class, C2SFetchMailHeaderPacket.class, ID.Packet.MailSync.C2SFetchMailPacket, Side.SERVER);
		mailSyncChannel.registerMessage(S2CMailHeaderPacket.Handler.class, S2CMailHeaderPacket.class, ID.Packet.MailSync.S2CMailBoxPartialDataPacket, Side.CLIENT);
		
		mailSyncChannel.registerMessage(C2SFetchMailContentPacket.Handler.class, C2SFetchMailContentPacket.class, ID.Packet.MailSync.C2SFetchMailContentPacket, Side.SERVER);
		mailSyncChannel.registerMessage(S2CMailContentPacket.Handler.class, S2CMailContentPacket.class, ID.Packet.MailSync.S2CMailContentPacket, Side.CLIENT);
		
		httpdChannel = NetworkRegistry.INSTANCE.newSimpleChannel(ID.Channel.httpd);
		
		httpdChannel.registerMessage(C2SFetchMailBoxURLPacket.Handler.class, C2SFetchMailBoxURLPacket.class, ID.Packet.MailHttpd.C2SFetchMailBoxURLPacket, Side.SERVER);
		httpdChannel.registerMessage(S2CMailBoxURLPacket.Handler.class, S2CMailBoxURLPacket.class, ID.Packet.MailHttpd.S2CMailBoxURLPacket, Side.CLIENT);
		
		httpdChannel.registerMessage(C2SCheckAccountPacket.Handler.class, C2SCheckAccountPacket.class, ID.Packet.MailHttpd.C2SCheckAccountPacket, Side.SERVER);
		httpdChannel.registerMessage(S2CAccountResultPacket.Handler.class, S2CAccountResultPacket.class, ID.Packet.MailHttpd.S2CAccountResultPacket, Side.CLIENT);
	}
}
