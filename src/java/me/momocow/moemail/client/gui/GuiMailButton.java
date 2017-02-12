package me.momocow.moemail.client.gui;

import java.util.UUID;

import me.momocow.mobasic.client.gui.widget.MoButton;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * Make original boolean 'enabled' to a new usage, 'isMailUnread'; true means the mail is unread, otherwise, false;
 * @author MomoCow
 *
 */
public class GuiMailButton extends MoButton
{
	private UUID mailID;
	
	public GuiMailButton(int buttonId, int x, int y, int widthIn, int heightIn) {
		super(buttonId, x, y, widthIn, heightIn, "", new ResourceLocation(Reference.MOD_ID, "textures/gui/mailbutton.png"));
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) 
	{
		return this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}
	
	@Override
	public boolean mouseClick(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
		if(super.mouseClick(mc, mouseX, mouseY, mouseButton))
		{
			//show mail
		}
		return false;
	}
	
	public void setMail(UUID mail, String mailTitle)
	{
		this.mailID = mail;
		this.displayString = mailTitle;
	}
	
	public UUID getMailId()
	{
		return this.mailID;
	}
	
	public void setUnread(Boolean isUnread)
	{
		this.enabled = isUnread;
	}
}
