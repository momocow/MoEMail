package me.momocow.moemail.client;

import java.util.Set;

import me.momocow.moemail.client.gui.GuiConfigMoEMail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiFactoryMoEMail implements IModGuiFactory
{
	@Override
	public void initialize(Minecraft minecraftInstance) {}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() 
	{
		return GuiConfigMoEMail.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() 
	{
		Thread.dumpStack();
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) 
	{
		Thread.dumpStack();
		return null;
	}

}
