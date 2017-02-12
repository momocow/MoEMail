package me.momocow.moemail.reference;

public class ID 
{
	/**
	 * Channel ID should be equal to or shorter than 20 letters; otherwise, decode exception will happen.
	 */
	public static final class Channel	
	{
		public static final String mailSync = "mailSyncChannel";
	}
	
	public static final class Packet
	{
		public static final int C2SFetchMailPacket = 1;
	}
	
	public static final class Gui
	{
		public static final int mailBox = 1;
	}
}
