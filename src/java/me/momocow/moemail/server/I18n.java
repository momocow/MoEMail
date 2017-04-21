package me.momocow.moemail.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import me.momocow.moemail.MoEMail;
import me.momocow.moemail.reference.Reference;

/**
 * <h2>Server-side translator for {@link MoHTTPD}</h2>
 * <ul><li>Use {@link I18n#setLang(String)} to set a language (or zh_TW will be used as default).</li>
 * <li>Use {@link I18n#format(String, String...)} to translate as vanilla method, {@linkplain net.minecraft.client.resources.I18n#format(String, Object...) I18n.format(String, String...)}, does.</li></ul>
 * <p>Note that all lang file should be encoded as UTF-8.</p>
 * @author MomoCow
 */
public class I18n 
{
	private static final Logger logger = MoEMail.logger;
	public static final String DEFAULT_LANG = "zh_TW";
	private static final Map<String, I18n> pool =  new HashMap<String, I18n>();
	private static I18n current;
	
	private String lang;
	private Map<String, String> dictionary = new HashMap<String, String>();

	private I18n(String langCode)
	{
		this.lang = langCode;
		InputStream input = this.getClass().getClassLoader().getResourceAsStream("assets/" + Reference.MOD_ID + "/www/lang/" + langCode + ".lang");
		
		if(input == null)
		{
			logger.warn("Lang file \"" + langCode + ".lang\" is not found at Path=assets/" + Reference.MOD_ID + "/www/lang/");
			logger.warn("Default lang file is used. " + "(assets/" + Reference.MOD_ID + "/www/lang/" + DEFAULT_LANG + ".lang)");
			
			this.lang = DEFAULT_LANG;
			input = this.getClass().getClassLoader().getResourceAsStream("assets/" + Reference.MOD_ID + "/www/lang/" + DEFAULT_LANG + ".lang");
		}
		
		if(input != null)
		{			
			try {
				List<String> content = IOUtils.readLines(input, StandardCharsets.UTF_8);
				
				for(String line: content)
				{
					int sep = line.indexOf("=");
					this.dictionary.put(line.substring(0, sep), line.substring(sep + 1));
				}
			} catch (IOException e) {
				logger.error("errors occurs when reading lang file at " + "assets/" + Reference.MOD_ID + "/www/lang/" + this.lang + ".lang");
				e.printStackTrace();
			}
			
			logger.info(this.dictionary.size() + " entries in " + this.lang + ".lang are loaded.");
		}
		else
		{
			logger.error("No lang file exists. Please check that your mod file has the complete resources or check out the version of your mod.");
		}
	}
	
	private static I18n get(String langCode)
	{
		if(!pool.containsKey(langCode))
		{
			pool.put(langCode, new I18n(langCode));
		}
		
		return pool.get(langCode);
	}
	
	public static void setLang(String langCode)
	{
		current = get(langCode);
	}
	
	public static String format(String key, Object... params)
	{
		if(current == null)
		{
			logger.warn("No language is specified. Default lang (" + DEFAULT_LANG + ") is used.");
			current = get(DEFAULT_LANG);
		}
		
		return String.format(current.dictionary.get(key), params);
	}
	
	public static String format(String langCode, String key, Object... params)
	{
		setLang(langCode);
		return format(key, params);
	}
}
