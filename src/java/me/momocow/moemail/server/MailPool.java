package me.momocow.moemail.server;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import me.momocow.moemail.MoEMail.Mail;
import me.momocow.moemail.MoEMail.Mail.Header;
import net.minecraft.util.math.MathHelper;

public class MailPool {
	private final File poolLog;
	private final File recvLog;
	private final File unreadLog;
	private String encoding = "UTF-8";
	
	private Map<UUID, Mail> pool = new HashMap<>();
	private Map<UUID, Set<UUID>> unread = new HashMap<>();
	private Map<UUID, Set<UUID>> recv = new HashMap<>();
	
	public MoEMail(File logDir, String enc)
	{
		this(logDir);
		this.encoding = enc;
	}
	
	public MoEMail(File logDir) 
	{
		File mailboxStorage = new File(logDir.getPath() + File.separator + "mailbox");
		this.poolLog = new File(mailboxStorage.getPath() + File.separator + "mailbox.log");
		this.recvLog = new File(mailboxStorage.getPath() + File.separator + "recv.log");
		this.unreadLog = new File(mailboxStorage.getPath() + File.separator + "unread.log");
		
		if(!mailboxStorage.exists())
		{
			mailboxStorage.mkdirs();
		}
		
		if(!this.poolLog.exists())
		{
			try
			{
				this.poolLog.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				LogHelper.error("Error creating the log file for mailbox.");
			}
		}
		
		if(!this.recvLog.exists())
		{
			try
			{
				this.recvLog.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				LogHelper.error("Error creating the log file for received mails.");
			}
		}
		
		if(!this.unreadLog.exists())
		{
			try
			{
				this.unreadLog.createNewFile();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				LogHelper.error("Error creating the log file for unread mails.");
			}
		}
	}
	
	public void load()
	{
		String poolJson = "";
		String recvJson = "";
		String unreadJson = "";
		
		try
		{
			poolJson = FileUtils.readFileToString(this.poolLog, this.encoding);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogHelper.error("Error loading mailbox.");
		}
		
		try
		{
			recvJson = FileUtils.readFileToString(this.recvLog, this.encoding);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogHelper.error("Error loading received mails.");
		}
		
		try
		{
			unreadJson = FileUtils.readFileToString(this.unreadLog, this.encoding);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogHelper.error("Error loading unread mails.");
		}
		
		if(!poolJson.isEmpty() && !recvJson.isEmpty() && !unreadJson.isEmpty())
		{
			Gson gson = new Gson();
			Type mailMap = new TypeToken<Map<UUID, Mail>>(){}.getType();
			Type midMap = new TypeToken<Map<UUID, Set<UUID>>>(){}.getType();
			
			this.pool = gson.fromJson(poolJson, mailMap);
			this.recv = gson.fromJson(recvJson, midMap);
			this.unread = gson.fromJson(unreadJson, midMap);
		}
	}
	
	public void save()
	{
		Gson gson = new Gson();
		
		try
		{
			FileUtils.writeStringToFile(this.poolLog, gson.toJson(this.pool), this.encoding);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogHelper.error("Error logging mailbox.");
		}
		
		try
		{
			FileUtils.writeStringToFile(this.recvLog, gson.toJson(this.recv), this.encoding);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogHelper.error("Error logging received mails.");
		}
		
		try
		{
			FileUtils.writeStringToFile(this.unreadLog, gson.toJson(this.unread), this.encoding);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LogHelper.error("Error logging unread mails.");
		}
	}
	
	public void send(UUID player, String from, String title, String msg)
	{
		
	}
	
	public void send(UUID player, String from, String msg)
	{
		this.send(player, from, , msg);
	}
	
	public static class Mail
	{
		private final UUID mid = MathHelper.getRandomUUID();
		private final Header header;
		private final String content;
		
		public Mail(UUID to, String from, String title, String c)
		{
			if(title.isEmpty()) title = "mail.MailBox.defaultTitle";
			this.header = new Header(to, from, title);
			this.content = c;
		}
		
		public UUID getId()
		{
			return this.mid;
		}
		
		public Timestamp getTimestamp()
		{
			return this.header.timestamp;
		}
		
		public UUID getReceiver()
		{
			return this.header.to;
		}
		
		public String getSender()
		{
			return this.header.from;
		}
		
		public String getTitle()
		{
			return this.header.title;
		}
		
		public String getContent()
		{
			return this.content;
		}
		
		private static final class Header
		{
			private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			private final UUID to;
			private final String from;
			private final String title;
			
			public Header(UUID t, String fr, String ttl)
			{
				this.to = t;
				this.from = fr;
				this.title = ttl;
			}
			
			@Override
			public String toString() 
			{
				Gson gson = new Gson();
				return gson.toJson(this);
			}
		}
	}
}
