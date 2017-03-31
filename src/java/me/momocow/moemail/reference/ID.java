package me.momocow.moemail.reference;

public class ID 
{
	/**
	 * Channel ID should be equal to or shorter than 20 letters; otherwise, decode exception will happen.
	 */
	public static final class Channel	
	{
		public static final String mailSync = "moemailSyncChannel";
		public static final String httpd = "moemailHttpdChannel";
	}
	
	public static final class Packet
	{
		public static final class MailSync
		{
			public static final int C2SFetchMailPacket = 1;
			public static final int S2CMailBoxPartialDataPacket = 2;
			public static final int C2SFetchMailContentPacket = 3;
			public static final int S2CMailContentPacket = 4;
			public static final int C2SMailDeletePacket = 5;
			public static final int S2CMailDeleteResponsePacket = 6;
			public static final int C2SMailInsertPacket = 7;
			public static final int S2CMailInsertResponsePacket = 8;
			public static final int S2CMailNotification = 9;
		}
		
		public static final class MailHttpd
		{
			public static final int C2SFetchMailBoxURLPacket = 1;
			public static final int S2CMailBoxURLPacket = 2;
			public static final int C2SCheckAccountPacket = 3;
			public static final int S2CAccountResultPacket = 4;
			public static final int C2SUpdatePasswdPacket = 5;
			public static final int S2CPasswdResultPacket = 6;
		}
	}
	
	public static final class Gui
	{
		public static final int mailBox = 1;
	}
}
