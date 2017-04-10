package me.momocow.moemail.client.gui;

import me.momocow.moemail.init.ModConfigs;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiConfigMoEMail extends GuiConfig
{
	private static final String LANG_KEY = "gui." + Reference.MOD_ID + "GuiConfigMoEMail";
	
	public GuiConfigMoEMail(GuiScreen parentScreen) 
	{
		super(parentScreen, 
				ModConfigs.general.getCategories(), 
				Reference.MOD_ID, 
				false, false, 
				I18n.format(LANG_KEY + ".title"), 
				I18n.format(LANG_KEY + ".contentTable"));
	}

	@Override
	public void initGui() 
	{
		super.initGui();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
