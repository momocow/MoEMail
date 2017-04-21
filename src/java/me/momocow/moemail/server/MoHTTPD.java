package me.momocow.moemail.server;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.UUID;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import jline.console.ConsoleReader;
import jline.internal.Log;
import me.momocow.mobasic.proxy.Server;
import me.momocow.mobasic.util.StorageFile;
import me.momocow.moemail.MoEMail;
import me.momocow.moemail.init.ModConfigs;
import me.momocow.moemail.reference.Reference;
import net.minecraft.server.management.UserListBans;
import net.minecraft.server.management.UserListIPBans;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;
import scala.actors.threadpool.Arrays;

/**
 * TODO 'IP ban' and 'Player Ban' have not tested yet
 * @author MomoCow
 *
 */
public class MoHTTPD extends NanoHTTPD
{
	private static Logger logger = MoEMail.logger;
	
	private static final String keystoreAsset = "assets/" + Reference.MOD_ID + "/ssl/keystore.jks";
	private static final String password = "me.momocow.moemail";
	private static final String promptKeystorePass = " KeyStore Password ";
	private static final String promptKeyPass = " Key Password ";
	
	//JWT
	private static final String AUTH_SCHEMA = "Bearer";
	private static final String ISSUER = Reference.MOD_ID + "@minecraft.momocow.me";
	private static final long EFFECTIVE_INTERVAL = 30 * 60 * 1000;
	private static final String MAILBOX ="MailBox";
	private static final String MAIL = "Mail";
	private static final String PERSONAL_INFO = "PersonalInfo";
	private static final String SUBJ_PLAYER = Reference.MOD_ID + "/";
	
	private static MoHTTPD instance;
	
	private final WWW www;
	
	private final StorageFile<HashMap<UUID, String>> fileUID2DigestMap;
//	private final StorageFile<HashSet<String>> fileBlacklistIP;
//	private StorageFile<HashSet<UUID>> fileBlacklistUser;
	private HashMap<UUID, String> mapUID2Digest = new HashMap<UUID, String>();
	
	private MoHTTPD() throws Exception
	{
		super(ModConfigs.httpd.hostname, ModConfigs.httpd.defaultPort);
		logger.info("Server is constructed.");
		
		this.www = new WWW("https://" + this.getHostname() + ":" + ModConfigs.httpd.defaultPort + "/");
		
		try
		{
			this.fileUID2DigestMap = new StorageFile<HashMap<UUID, String>>(new HashMap<UUID, String>(), new File(MailPool.instance().getStorageDir(), "authen.log"), logger);
			this.loadData();
		}
		catch(Exception e)
		{
			logger.warn("Unable to load Player accounts.");
			throw e;
		}
				
		//use custom keystore
		if(!ModConfigs.httpd.customKeystoreURI.isEmpty())
		{
			Log.info("Using custom keystore...");
			
			KeyStore keystore;
			KeyManagerFactory kmf;
			
			try
			{
				keystore = KeyStore.getInstance(KeyStore.getDefaultType());
				kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			}
			catch(Exception e)
			{
				logger.warn("Error occurs when getting instance of KeyStore and KeyManagerFactory.", e);
				throw e;
			}
			
			String keyStorePassword = "";
			String keyPassword = "";
			
			//ask for keystore password
			if(GraphicsEnvironment.isHeadless())
			{
				keyStorePassword = new ConsoleReader().readLine(promptKeystorePass, '*');
				keyPassword = new ConsoleReader().readLine(promptKeyPass, '*');
			}
			else
			{
				Box box1 = Box.createHorizontalBox();
				box1.add(new JLabel(promptKeystorePass));
				JPasswordField jpf1 = (JPasswordField)box1.add(new JPasswordField(24));

				int button = JOptionPane.showConfirmDialog(null, box1, promptKeystorePass, JOptionPane.OK_CANCEL_OPTION);
				if (button == JOptionPane.OK_OPTION) {
					keyStorePassword = new String(jpf1.getPassword());
				}
				else
				{
					throw new KeyStoreException("Bad Keystore password. Fail to read custom keystore file due to the rejection by the dedicated server admin.");
				}
				
				Box box2 = Box.createVerticalBox();
				box2.add(new JLabel(promptKeystorePass));
				JPasswordField jpf2 = (JPasswordField)box2.add(new JPasswordField(24));
				
				button = JOptionPane.showConfirmDialog(null, box2, promptKeyPass, JOptionPane.OK_CANCEL_OPTION);
				if (button == JOptionPane.OK_OPTION) {
					keyPassword = new String(jpf2.getPassword());
				}
				else
				{
					throw new KeyStoreException("Bad Key password. Fail to read custom keystore file due to the rejection by the dedicated server admin.");
				}
			}
			
			try {
				keystore.load(new FileInputStream(ModConfigs.httpd.customKeystoreURI), keyStorePassword.toCharArray());
				kmf.init(keystore, keyPassword.toCharArray());
			} catch (Exception e) {
				logger.warn("Error occurs when loading custom keystore.", e);
				throw e;
			}
			
			this.makeSecure(NanoHTTPD.makeSSLSocketFactory(keystore, kmf), null);
		}
		else
		{
			logger.info("Using default keystore...");
			
			//make the server serve HTTPs connection
			this.makeSecure(MoHTTPD.makeSSLSocketFactory(keystoreAsset, password.toCharArray()), null);
		}
		
		logger.info("Server is made to serve HTTPs connection");
	}
	
	public DecodedJWT verify(IHTTPSession session, String jwtString)
	{
		DecodedJWT jwt = null;
		
        try 
        {
        	JWT raw = JWT.decode(jwtString);
        	String whoYouAre = raw.getSubject();
        	String digest = this.mapUID2Digest.get(UUID.fromString(new String(Base64.decodeBase64(whoYouAre.replace(SUBJ_PLAYER, "")), "UTF-8")));
        	
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(this.makeSecret(digest, session.getRemoteIpAddress())))
					.withIssuer(ISSUER)
					.withAudience(MAILBOX, MAIL, PERSONAL_INFO)
					.build();
			jwt = verifier.verify(jwtString);
		} 
        catch(JWTVerificationException jve)
        {
        	logger.info("Invalid JWT from [" + session.getRemoteIpAddress() +"]. ", jwtString);
        }
        catch (Exception e) 
        {
			logger.warn("Error occurs when verifying the token. ", jwtString);
		}

        return jwt;
	}
	
	public boolean authorize(DecodedJWT user, IHTTPSession session)
	{
		UserListBans blacklist  = Server.getBannedPlayers();
		GameProfile profile = Server.getProfile(this.getPlayerIdByJWT(user));
		
		if(blacklist.isBanned(profile))
		{
			return false;
		}
		
		return true;
	}
	
	public boolean isIPBanned(IHTTPSession session)
	{
		UserListIPBans blacklist = Server.getBannedIPs();
		
		return blacklist.getEntry(session.getRemoteIpAddress()) != null;
	}
	
	/**
	 * Log the user in with uid and digested password
	 * @param usr
	 * @param pw
	 * @return
	 */
	public boolean authenticate(UUID usr, String pw)
	{
		String digest = null;
		try {
			digest = MoHTTPD.digest(pw);
		} catch (NoSuchAlgorithmException e) {
			logger.info("Fail to digest the password when authentication. ", e);
		}
		
		if(this.mapUID2Digest.get(usr) != null && this.mapUID2Digest.get(usr).equals(digest))
		{
			//operation after user successfully logging in
			return true;
		}

		return false;
	}
	
	private Response createToken(UUID uid, String pw, String ip)
	{
		Response response = null;
		DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		long sysTime = System.currentTimeMillis();
		Date isa = new Date(sysTime);
		Date exp = new Date(sysTime + EFFECTIVE_INTERVAL);
		try
		{
			String token = JWT.create()
					.withIssuer(ISSUER)
					.withAudience(MAILBOX, MAIL, PERSONAL_INFO)
					.withExpiresAt(exp)
					.withSubject(SUBJ_PLAYER + Base64.encodeBase64String(uid.toString().getBytes("UTF-8")))
					.withJWTId(MathHelper.getRandomUUID().toString())
					.withIssuedAt(isa)
					.sign(Algorithm.HMAC256(this.makeSecret(pw, ip)));
			response =  this.getDefaultRedirect(Errno.NOTHING);
			response.addHeader("Set-Cookie", Reference.MOD_ID + "-jwt=" + token + "; Expires=" + df.format(exp) + "; Domain=" + this.getHostname() + "; Path=/; HttpOnly");
		} 
		catch (Exception e)
		{
			logger.warn("Unable to create a token for client. ", e);
		}
				
		return response;
	}
	
	private Lang getReqLang(IHTTPSession session, DecodedJWT jwt)
	{
		
	}
	
	@Override
    public Response serve(IHTTPSession session) 
	{
		Response response = null;

		if(this.isIPBanned(session))
		{
			response = newFixedLengthResponse("Banned IPs");
			response.setStatus(Status.FORBIDDEN);
		}
		else
		{
			Map<String, String> headers = session.getHeaders();
	        Method method = session.getMethod();
	        
	        String jwtString = null;
	        if(headers.containsKey("authorization"))
	        {
	        	String[] auth = headers.get("Authorization").trim().split(" ");
	        	if(auth[0].equals(AUTH_SCHEMA))
	        	{
	        		jwtString = auth[1];
	        	}
	        }
	        else if(headers.containsKey("cookie"))
	        {
	        	Map<String, List<String>> cookies = this.parseCookie(headers.get("cookie"));
	        	jwtString = this.getLastInList(cookies.get(Reference.MOD_ID + "-jwt"));
	        }
	        
	        DecodedJWT jwt = (jwtString != null && !jwtString.isEmpty())? this.verify(session, jwtString): null;
	        Lang lang = this.getReqLang(session, jwt);
	        if(jwt != null) //authenticated and authorized connections
	        {
	        	if(this.authorize(jwt, session))
	        	{
		        	if(session.getUri().matches("/{0,1}") || session.getUri().matches("/home/{0,1}"))
		        	{
		        		Document home = this.getPage(Page.HOMEPAGE);
		        		home.getElementById("greet").appendText(this.getPlayerNameByJWT(jwt));
		        		response = newFixedLengthResponse(home.outerHtml());
		        		response.setStatus(Status.OK);
		        	}
		        	else if(session.getUri().matches("/auth/logout/{0,1}"))
		        	{
		        		response =  this.getDefaultRedirect(Errno.NOTHING);
		    			response.addHeader("Set-Cookie", Reference.MOD_ID + "-jwt=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Domain=" + this.getHostname() + "; Path=/; HttpOnly");
		        	}
	        	}
	        	else
	        	{
	        		response =  this.getDefaultRedirect(Errno.BANNEDPLAYER);
	    			response.addHeader("Set-Cookie", Reference.MOD_ID + "-jwt=; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Domain=" + this.getHostname() + "; Path=/; HttpOnly");
	        	}
	        }
	        else	//unauthenticated connections
	        {
	        	if(session.getUri().matches("/test/{0,1}"))
	        	{
	        		response = newFixedLengthResponse("Hello");
	        		response.setStatus(Status.OK);
	        	}
	        	else if(session.getUri().matches("/auth/login/{0,1}") && method == Method.POST)
	        	{
	        		try {
						session.parseBody(new HashMap<String, String>());
					} catch (Exception e) {
						logger.warn("Error occurs when fetching request parameters. ", e);
					}
	        		
	        		Map<String, List<String>>params = session.getParameters();
	        		if(params.get("username") != null && params.get("password") != null)
	        		{
		        		UUID uid = Server.getPlayerId(this.getLastInList(params.get("username")));
		        		if(uid != null)
		        		{
							if(this.authenticate(uid, this.getLastInList(params.get("password"))))
			        		{
			        			response = this.createToken(uid, this.mapUID2Digest.get(uid), session.getRemoteIpAddress());
			        		}
		        		}
	        		}
	        		if(response == null) response =  this.getDefaultRedirect(Errno.ERRLOGIN);
	        	}
	        	else  //show entrance for user log-in
	        	{
	        		String feedback = "";
	        		if(session.getParameters().containsKey("fyi"))
	        		{
		        		List<String> fyi = session.getParameters().get("fyi");
		        		if(fyi != null)
	        			{
		        			switch(Errno.getErrno(Integer.decode(fyi.get(fyi.size() - 1))))
		        			{
		        				case BANNEDPLAYER:
		        					feedback = I18n.format(langCode, key, params);
		        					break;
		        				case ERRLOGIN:
		        					feedback = "Oops! Something is wrong.";
		        				case NOTHING:
		        				default:
		        			}
	        			}
	        		}
	        		
	        		Set<String> classFeedback = new HashSet<String>();
	        		classFeedback.add("ui");
	        		classFeedback.add("message");
	        		classFeedback.add("error");
	        		
	        		Document entrance = this.getPage(Page.ENTRANCE);
	        		Element divError = new Element("div").classNames(classFeedback)
	        				.appendChild(new Element("div").addClass("header").text(""))
	        				.appendChild(new Element("p").text(feedback));
	        		entrance.select("#login").get(0).appendChild();
		        	response = newFixedLengthResponse(entrance.outerHtml());
		        	response.addHeader("WWW-Authenticate", "realm=\"" + Reference.MOD_NAME +"\"");
		            response.setStatus(Status.UNAUTHORIZED);
	            }
	        }
	
	        if(response == null) response =  this.getDefaultRedirect(Errno.NOTHING);
		}	
        
        return response;
    }
	
	private String getPlayerNameByJWT(DecodedJWT jwt)
	{
		UUID uid = this.getPlayerIdByJWT(jwt);
		
		if(uid != null)
		{
			return Server.getPlayerName(uid);
		}
		
		return "";
	}
	
	public UUID getPlayerIdByJWT(DecodedJWT jwt)
	{
		UUID uid = null;
		try {
			uid = UUID.fromString(new String(Base64.decodeBase64(jwt.getSubject().replace(SUBJ_PLAYER, "")), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.warn("Fail to decode the user id from JWT. ");
		}
		
		return uid;
	}
	
	public Response getDefaultRedirect(Errno msg)
	{
		String err = "";
		
		if(msg != Errno.NOTHING)
		{
			err = "?fyi=" + msg.toString();
		}

		Response response = newFixedLengthResponse("");
		response.addHeader("Location", this.www.baseURI.toString() + err);
		response.setStatus(Status.REDIRECT_SEE_OTHER);
		response.setRequestMethod(Method.GET);
		return response;
	}
	
	public Document getPage(Page page)
	{
		Document doc = null;
		
		switch(page)
		{
			case ENTRANCE:
				if(ModConfigs.httpd.www.entrancePage.isEmpty()) //default page
				{
					doc = this.www.getEntrancePage();
				}
				else	//custom page
				{
					try {
						doc = Jsoup.parse(new FileInputStream(ModConfigs.httpd.www.entrancePage), "UTF-8", this.www.getURL(page));
					} catch (IOException e) {
						logger.warn("Fail to read the custom entrance page. ", e);
					}
				}
				break;
			case HOMEPAGE:
				if(ModConfigs.httpd.www.homePage.isEmpty()) //default page
				{
					doc = this.www.getHomePage();
				}
				else	//custom page
				{
					try {
						doc = Jsoup.parse(new FileInputStream(ModConfigs.httpd.www.homePage), "UTF-8", this.www.getURL(page));
					} catch (IOException e) {
						logger.warn("Fail to read the custom entrance page. ", e);
					}
				}
				break;
			default:
				doc = new Document(this.www.getURL(Page.ENTRANCE));
		}
		
		return doc;
	}
	
	private String makeSecret(String seed, String ip)
	{
		try
		{
			return MoHTTPD.digest(seed + MoHTTPD.digest(ip));
		}
		catch(Exception e)
		{
			logger.warn("Fail to create secret. ", e);
		}
		
		return null;
	}
	
	private Map<String, List<String>> parseCookie(String parms)
	{
		 Map<String, List<String>> p = new HashMap<String, List<String>>();
		 
        if (parms == null) {
            return p;
        }

        StringTokenizer st = new StringTokenizer(parms, ";");
        while (st.hasMoreTokens()) {
            String e = st.nextToken();
            int sep = e.indexOf('=');
            
            String key = e.substring(0, sep);
            String value = e.substring(sep + 1);

            List<String> values = p.get(key);
            if (values == null) {
                values = new ArrayList<String>();
                p.put(key, values);
            }

            values.add(value);
        }
        
        return p;
	}
	
	public void testConnection()
	{
		Thread testThread = new Thread(new Runnable(){
			@Override
			public void run() {
				logger.info("* [Connection test] Start.");
				
				URL serverURL = null;
				try {
					String urlString = (ModConfigs.httpd.hostname.isEmpty())? "https://" + InetAddress.getLocalHost().getHostAddress() + ":" + MoHTTPD.instance.getListeningPort() + "/test/"
							: "https://" + InetAddress.getByName(ModConfigs.httpd.hostname).getHostAddress() + ":" + MoHTTPD.instance.getListeningPort() + "/test/";
					serverURL = new URL(urlString);
					logger.info("* [Connection test] URL: " + serverURL);
					
					TrustManager[] trustMyCerts = new TrustManager[] { new X509TrustManager(){
						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
								throws java.security.cert.CertificateException {}
						
						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
								throws java.security.cert.CertificateException {}
						@Override
						public java.security.cert.X509Certificate[] getAcceptedIssuers() 
						{
							return null;
						}
					}};
					
					 HostnameVerifier hv = new HostnameVerifier()
					 {
						 public boolean verify(String urlHostName, SSLSession session)
			             {
			                 return urlHostName.equalsIgnoreCase(session.getPeerHost());
			             }
					 };
				 
				
					 SSLContext sc = SSLContext.getInstance("SSL");
			         sc.init(null, trustMyCerts, new java.security.SecureRandom());  
			         
			         HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			         HttpsURLConnection.setDefaultHostnameVerifier(hv);
			         HttpsURLConnection con = (HttpsURLConnection)serverURL.openConnection();
			         
			         con.setRequestMethod("GET");
			         con.setUseCaches(false);
			         con.setRequestProperty("Content-Length", String.valueOf("Hello".getBytes("UTF-8").length));
			         con.setConnectTimeout(1000);

		        	 logger.info("* [Connection test] Connecting...");

		        	 String line = null;
		        	 BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		        	 while((line = in.readLine()) != null)
		        	 {
		        		 logger.info("* [Connection test] Response: " + line);
		        	 }
		        	 
		        	 logger.info("* [Connection test] Disconnecting...");
		        	 con.disconnect();
			         
			         logger.info("Connection test succeeds.");
			         
			         return;
				} 
				catch(SocketTimeoutException sockEx)
				{
					logger.info("* [Connection test] Connection failed.");
					 JOptionPane.showMessageDialog(null, "<html><head><style>body{max-width:500px;}</style></head><body><p>It is a connection timeout notice,which indicates the external network cannot access your MoEMail server.</p>"
					 		+ "<p>If it is what you want, IGNORE the notice; otherwise, check the firewall setting and make sure the hostname in the config, moemail-httpd.cfg, match your NIC setting.</p></body></html>", 
					 		"Fail to test the connection", JOptionPane.WARNING_MESSAGE);
		        	
		        	 return;
				}
				catch (Exception e) 
				{
					Box errlog = Box.createVerticalBox();
					JTextArea area = new JTextArea(e.toString());
					area.setFont(Font.decode(Font.MONOSPACED));
					errlog.add(area);
					
					JOptionPane.showMessageDialog(null, errlog, "Fail to test the connection", JOptionPane.WARNING_MESSAGE);
					return;
				} 
			}
		});
		
		testThread.setName("Connection Test");
		testThread.start();
	}
	
	private MoHTTPD loadData() throws Exception
	{
		HashMap<UUID, String> loaded = this.fileUID2DigestMap.load();
		this.mapUID2Digest = (loaded == null)? this.mapUID2Digest: loaded;
		return this;
	}
	
	private MoHTTPD saveData() throws Exception
	{
		this.fileUID2DigestMap.save(this.mapUID2Digest);
		return this;
	}
	
	private String getLastInList(List<String> params)
	{
		if(params != null && params.size() > 0) return params.get(params.size() - 1);
		return "";
	}
	
	public void registerUser(UUID uid, String rawPass)
	{
		try
		{
			this.mapUID2Digest.put(uid, MoHTTPD.digest(rawPass));
		}
		catch(Exception e)
		{
			logger.warn("Fail to register the new user. ", e);
		}
		
		try 
		{
			this.saveData();
		} 
		catch (Exception e) 
		{
			logger.warn("Fail to save the new user. ");
		}
	}
	
	public void registerUser(String username, String rawPass)
	{
		this.registerUser(Server.getPlayerId(username), rawPass);
	}
	
	public String getURL()
	{
		return MoHTTPD.instance.www.getURL(Page.ENTRANCE);
	}
	
	public boolean hasUser(UUID uid)
	{
		return this.mapUID2Digest.get(uid) != null;
	}
		
	public static final MoHTTPD instance()
	{
		return MoHTTPD.instance;
	}
	
	private static String digest(String text) throws NoSuchAlgorithmException
	{
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			logger.warn("Can't encrypt the password. ", e);
			throw e;
		}
		
		try 
		{
			messageDigest.update(text.getBytes("UTF-8"));
		} 
		catch (UnsupportedEncodingException e)
		{
			messageDigest.update(text.getBytes());
		}

		return new String(messageDigest.digest());
	}
	
	public static MoHTTPD init(FMLPostInitializationEvent e) throws Exception
	{
		if((!ModConfigs.general.httpd.isDedicatedServerOnly || e.getSide() == Side.SERVER) && MoHTTPD.instance == null)
		{
			try {
				MoHTTPD.instance = new MoHTTPD();
			} catch (Exception e1) {
				logger.warn("Fail to init the HTTP server.", e1);
				throw e1;
			}

			return MoHTTPD.instance;
		}
		
		return MoHTTPD.instance;
	}
	
	public static MoHTTPD start(FMLServerStartingEvent e)
	{
		if((!ModConfigs.general.httpd.isDedicatedServerOnly || e.getSide() == Side.SERVER) && MoHTTPD.instance != null)
		{
			try
			{
				MoHTTPD.instance.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			} catch (IOException e1) {
				logger.warn("Fail to start the HTTP server for MoEMail.", e1);
				return MoHTTPD.instance = null;
			}
			
			MoHTTPD.instance.testConnection();
			
			logger.info("HTTPs Server is running.");
			return MoHTTPD.instance;
		}
		return MoHTTPD.instance;
	}
	
	public static void stop(FMLServerStoppingEvent e)
	{
		if((!ModConfigs.general.httpd.isDedicatedServerOnly || e.getSide() == Side.SERVER) && MoHTTPD.instance != null)
		{
			logger.info("Server is stopping.");
			MoHTTPD.instance.stop();
		}
	}
	
	//custom one due to MC class loader
	public static SSLServerSocketFactory makeSSLSocketFactory(String keyAndTrustStoreClasspathPath, char[] passphrase) throws IOException 
	{
		logger.info("Making secure connection...");
		
		InputStream keystoreStream = null;
        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystoreStream = getResourceAsStream(keyAndTrustStoreClasspathPath);

            if (keystoreStream == null) {
                throw new IOException("Unable to load keystore from classpath: " + keyAndTrustStoreClasspathPath);
            }

            keystore.load(keystoreStream, passphrase);
            if(keystoreStream != null)
        	{
        		keystoreStream.close();
        	}
            
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, passphrase);
            return NanoHTTPD.makeSSLSocketFactory(keystore, keyManagerFactory);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
	
	public static InputStream getResourceAsStream(String path)
	{
		return MoHTTPD.class.getClassLoader().getResourceAsStream(path);
	}
	
	private static class WWW
	{
		//WWW
		public final URL baseURI;
		
		private static final String HTML_ENTRANCE = "assets/" + Reference.MOD_ID + "/www/entrance.html";
		private static final String HTML_HOME = "assets/" + Reference.MOD_ID + "/www/home.html";
		
		public WWW(String url) throws Exception
		{
			this.baseURI = new URL(url);
		}
		
		public Document getEntrancePage()
		{
			String url = getURL(Page.ENTRANCE);
			Document doc = new Document(url);
			InputStream is = null;
			try {
				doc = Jsoup.parse(is = getResourceAsStream(HTML_ENTRANCE), "UTF-8", url);
				if(is != null)
				{
					is.close();
				}
			} catch (IOException e) {
				logger.warn("Fail to read the entrance page. ", e);
			}
			
			return doc;
		}
		
		public Document getHomePage()
		{
			String url = getURL(Page.HOMEPAGE);
			Document doc = new Document(url);
			InputStream is = null;
			try {
				doc = Jsoup.parse(is = getResourceAsStream(HTML_HOME), "UTF-8", url);
				if(is != null)
				{
					is.close();
				}
			} catch (IOException e) {
				logger.warn("Fail to read the entrance page. ", e);
			}
			return doc;
		}
		
		public String getURL(Page page)
		{
			switch(page)
			{
				case HOMEPAGE:
					return this.baseURI + "home";
				case ENTRANCE:
				default:
					return this.baseURI.toString();	
			}
		}
	}
	
	private enum Page
	{
		ENTRANCE,
		HOMEPAGE
	}
	
	private enum Errno
	{
		NOTHING(0),
		ERRLOGIN(1),
		BANNEDPLAYER(2);
		
		private int errno;
		
		private Errno(int e)
		{
			this.errno = e;
		}
		
		public String toString()
		{
			return String.valueOf(this.errno);
		}
		
		public static Errno getErrno(int i)
		{
			switch(i)
			{
				case 2:
					return Errno.BANNEDPLAYER;
				case 1:
					return Errno.ERRLOGIN;
				case 0:
				default:
					return Errno.NOTHING;
			}
		}
	}
	
	public enum Lang
	{
		zh_TW("zh_TW", "tw"), en_US("en_US", "en");
		
		private static List<Lang> langs = new ArrayList<Lang>();
		private String lang;
		private Set<String> keywords = new HashSet<String>();
		
		private Lang(String lang, String... keys)
		{
			this.lang = lang;
			this.keywords.addAll(Sets.newHashSet(keys));
			register(this);
		}
		
		public boolean is(String key)
		{
			return this.keywords.contains(key);
		}
		
		private static void register(Lang l)
		{
			langs.add(l);
		}
		
		public static Lang getLang(String key)
		{
			for(Lang l: langs)
			{
				if(l.is(key))
				{
					return l;
				}
			}
			
			return zh_TW;
		}
	}
}
