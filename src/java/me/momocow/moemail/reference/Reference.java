package me.momocow.moemail.reference;

public class Reference 
{
	//mod ref
	public static final String MOD_ID = "moemail";
    public static final String MOD_NAME = "MoEMail";
    public static final String DOMAIN = "me.momocow";
    public static final String VERSION = "0.0.1.0";
    public static final String DEPENDENCIES = "required-after:Forge@[12.17.0.2051,);required-after:MoBasic@[0.0.1.0,)";
    
    //special class
    public static final String GUIFACTORY = DOMAIN + "." + MOD_ID + ".client.GuiFactoryMoEMail";
    public static final String CLIENTPROXY = DOMAIN + "." + MOD_ID + ".proxy.ClientProxy";
    public static final String SERVERPROXY = DOMAIN + "." + MOD_ID + ".proxy.ServerProxy";
}
