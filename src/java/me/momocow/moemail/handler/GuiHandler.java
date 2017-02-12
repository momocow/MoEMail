package me.momocow.moemail.handler;

import me.momocow.moemail.client.gui.GuiMailBox;
import me.momocow.moemail.reference.ID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) 
	{
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) 
	{
		switch(id)
		{
			case ID.Gui.mailBox:
				return new GuiMailBox();
			default:
				return null;
		}
	}

}
