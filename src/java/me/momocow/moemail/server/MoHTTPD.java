package me.momocow.moemail.server;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fi.iki.elonen.NanoHTTPD;
import me.momocow.moemail.config.Config;
import me.momocow.moemail.reference.Reference;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;

public class MoHTTPD extends NanoHTTPD
{
	private static Logger logger = LogManager.getLogger("MoHTTPD");
	private static MoHTTPD instance;
	
	private static final String kestoreJKSFile = "keystore.jks";
	private static final String keystoreAsset = "assets/" + Reference.MOD_ID + "/ssl/" + kestoreJKSFile;
	private static final String keystoreStorageDir = "data/";
	private static final String password = "me.momocow.moemail";
			
	private MoHTTPD() throws IOException
	{
		super(Config.HTTPD.defaultPort);
		logger.info("Sevrer is constructed.");
		
//		//generate the keystore if it does not exist
//		File keystore = new File(keystoreStorageDir, kestoreJKSFile);
//		if(!keystore.exists())
//		{
//			BufferedReader in = new BufferedReader(
//					new InputStreamReader(
//					getClass().
//					getClassLoader()
//					.getResourceAsStream(keystoreAsset)));
//			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(FileUtils.openOutputStream(keystore)));
//			String line = null;
//			
//			try
//			{
//				while( (line = in.readLine()) != null)
//				{
//					out.write(line);
//					out.newLine();
//				}
//				out.flush();
//			}
//			catch(IOException e)
//			{
//				throw e;
//			}
//			finally
//			{
//				in.close();
//				out.close();
//			}
//		}
		
		//make the server serve HTTPs connection
		this.makeSecure(MoHTTPD.makeSSLSocketFactory(keystoreAsset, password.toCharArray()), null);
		
		logger.info("Server is made to serve HTTPs connection");
	}
	
	/**
	 * GET /
	 * @param session
	 * @return
	 */
	private String handleGET(IHTTPSession session)
	{
		return "<pre>"+session.getParameters().toString()+"</pre>";
	}
	
	@Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        String response = null;
        
        switch(method)
        {
        	case GET:
        		response = this.handleGET(session);
        		break;
        	default:
        		return super.serve(session);
        }
		
        return newFixedLengthResponse(response);
    }
	public static MoHTTPD init(FMLPostInitializationEvent e)
	{
		if(e.getSide() == Side.SERVER && MoHTTPD.instance == null)
		{
			try {
				MoHTTPD.instance = new MoHTTPD();
			} catch (IOException e1) {
				logger.warn("Fail to init the HTTP server for MoEMail.", e1);
				MoHTTPD.instance = null;
			}

			return MoHTTPD.instance;
		}
		
		return null;
	}
	
	public static MoHTTPD start(FMLServerStartingEvent e)
	{
		if(e.getSide() == Side.SERVER && MoHTTPD.instance != null)
		{
			try
			{
				MoHTTPD.instance.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
			} catch (IOException e1) {
				logger.warn("Fail to start the HTTP server for MoEMail.", e1);
				return MoHTTPD.instance = null;
			}
			
			logger.info("Server is running.");
			return MoHTTPD.instance;
		}
		return null;
	}
	
	public static void stop(FMLServerStoppingEvent e)
	{
		if(e.getSide() == Side.SERVER && MoHTTPD.instance != null)
		{
			MoHTTPD.instance.stop();
		}
	}
	
	public static SSLServerSocketFactory makeSSLSocketFactory(String keyAndTrustStoreClasspathPath, char[] passphrase) throws IOException 
	{
        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream keystoreStream = MoHTTPD.class.getClassLoader().getResourceAsStream(keyAndTrustStoreClasspathPath);

            if (keystoreStream == null) {
                throw new IOException("Unable to load keystore from classpath: " + keyAndTrustStoreClasspathPath);
            }

            keystore.load(keystoreStream, passphrase);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keystore, passphrase);
            return NanoHTTPD.makeSSLSocketFactory(keystore, keyManagerFactory);
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
