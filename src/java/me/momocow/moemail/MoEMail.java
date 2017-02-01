package me.momocow.moemail;

import me.momocow.moemail.reference.Reference;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, serverSideOnly = true)
public class MoEMail
{
	@Mod.Instance(Reference.MOD_ID)
	public static MoEMail instance;
	
	
}
