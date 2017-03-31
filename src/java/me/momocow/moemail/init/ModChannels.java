package me.momocow.moemail.init;

import me.momocow.moemail.network.C2SCheckAccountPacket;
import me.momocow.moemail.network.C2SFetchMailBoxURLPacket;
import me.momocow.moemail.network.C2SFetchMailContentPacket;
import me.momocow.moemail.network.C2SFetchMailHeaderPacket;
import me.momocow.moemail.network.C2SMailDeletePacket;
import me.momocow.moemail.network.C2SMailInsertPacket;
import me.momocow.moemail.network.C2SUpdatePasswdPacket;
import me.momocow.moemail.network.S2CAccountResultPacket;
import me.momocow.moemail.network.S2CMailBoxURLPacket;
import me.momocow.moemail.network.S2CMailContentPacket;
import me.momocow.moemail.network.S2CMailDeleteResponsePacket;
import me.momocow.moemail.network.S2CMailHeaderPacket;
import me.momocow.moemail.network.S2CMailInsertResponsePacket;
import me.momocow.moemail.network.S2CMailNotification;
import me.momocow.moemail.network.S2CPasswdResultPacket;
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
		
		mailSyncChannel.registerMessage(C2SMailDeletePacket.Handler.class, C2SMailDeletePacket.class, ID.Packet.MailSync.C2SMailDeletePacket, Side.SERVER);
		mailSyncChannel.registerMessage(S2CMailDeleteResponsePacket.Handler.class, S2CMailDeleteResponsePacket.class, ID.Packet.MailSync.S2CMailDeleteResponsePacket, Side.CLIENT);
		
		mailSyncChannel.registerMessage(C2SMailInsertPacket.Handler.class, C2SMailInsertPacket.class, ID.Packet.MailSync.C2SMailInsertPacket, Side.SERVER);
		mailSyncChannel.registerMessage(S2CMailInsertResponsePacket.Handler.class, S2CMailInsertResponsePacket.class, ID.Packet.MailSync.S2CMailInsertResponsePacket, Side.CLIENT);
		
		mailSyncChannel.registerMessage(S2CMailNotification.Handler.class, S2CMailNotification.class, ID.Packet.MailSync.S2CMailNotification, Side.CLIENT);
		
		httpdChannel = NetworkRegistry.INSTANCE.newSimpleChannel(ID.Channel.httpd);
		
		httpdChannel.registerMessage(C2SFetchMailBoxURLPacket.Handler.class, C2SFetchMailBoxURLPacket.class, ID.Packet.MailHttpd.C2SFetchMailBoxURLPacket, Side.SERVER);
		httpdChannel.registerMessage(S2CMailBoxURLPacket.Handler.class, S2CMailBoxURLPacket.class, ID.Packet.MailHttpd.S2CMailBoxURLPacket, Side.CLIENT);
		
		httpdChannel.registerMessage(C2SCheckAccountPacket.Handler.class, C2SCheckAccountPacket.class, ID.Packet.MailHttpd.C2SCheckAccountPacket, Side.SERVER);
		httpdChannel.registerMessage(S2CAccountResultPacket.Handler.class, S2CAccountResultPacket.class, ID.Packet.MailHttpd.S2CAccountResultPacket, Side.CLIENT);
		
		httpdChannel.registerMessage(C2SUpdatePasswdPacket.Handler.class, C2SUpdatePasswdPacket.class, ID.Packet.MailHttpd.C2SUpdatePasswdPacket, Side.SERVER);
		httpdChannel.registerMessage(S2CPasswdResultPacket.Handler.class, S2CPasswdResultPacket.class, ID.Packet.MailHttpd.S2CPasswdResultPacket, Side.CLIENT);
	}
}
