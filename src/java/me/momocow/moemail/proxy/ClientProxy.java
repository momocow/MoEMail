package me.momocow.moemail.proxy;

import me.momocow.moemail.init.ModKeyBindings;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ClientProxy extends CommonProxy
{
	public void registerKeyBindings(FMLInitializationEvent e)
	{
		ModKeyBindings.init(e);
	}
	
	@Override
	public void init(FMLInitializationEvent e) throws Exception 
	{
		super.init(e);
		this.registerKeyBindings(e);
	}
}
