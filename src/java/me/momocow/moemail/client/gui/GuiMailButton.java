package me.momocow.moemail.client.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.UUID;

import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoButton;
import me.momocow.moemail.reference.Reference;
import me.momocow.moemail.server.MailPool.Mail.Header;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

/**
 * Make original boolean 'enabled' to a new usage, 'isMailUnread'; true means the mail is unread, otherwise, false;
 * @author MomoCow
 *
 */
public class GuiMailButton extends MoButton
{
	private Header head;
	private DateFormat df = new SimpleDateFormat("MM.dd HH:mm");
	
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
			if(mc.currentScreen instanceof GuiMailBox && this.hasMail())
			{
				this.head.setRead();
				this.setUnread(this.head.isUnread());
				mc.displayGuiScreen(new GuiMail((GuiMailBox) mc.currentScreen, this.head));
			}
		}
		return false;
	}
	
	public UUID getMailId()
	{
		return this.head.getId();
	}
	
	public void setUnread(Boolean isUnread)
	{
		this.enabled = isUnread;
	}
	
	public boolean isUnread()
	{
		return this.enabled;
	}
	
	public void setMail(Header head)
	{		
		if(head == null)
		{
			this.head = head;
			this.setUnread(false);
		}
		else
		{
			this.head = head;
			this.setUnread(head.isUnread());
		}
	}
	
	public boolean hasMail()
	{
		return this.head != null;
	}
	
	public Header getMail()
	{
		return this.head;
	}
	
	@Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    	if (this.visible)
        {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            MoGuiScreen.drawProportionTexturedRect(this.TEXTURE, this.xPosition, this.yPosition, this.zLevel, 0, (this.isUnread())? 18: 0, this.width, this.height, 198, 36, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (this.hovered)
            {
                j = 16777120;
            }
            
            if(this.hasMail())
            {
	            //button width = 198, margin left = 5, margin right = 5
	            int divSize1 = 50;
	            int divSize2 = 50;
	            int divSize3 = 88;
	           
	            //date time
	            this.drawString(mc.fontRendererObj, mc.fontRendererObj.trimStringToWidth(this.df.format(head.getTimestamp()), divSize1 - 10), this.xPosition + 5, this.yPosition + 5, j);
	            
	            //sender
	            this.drawString(mc.fontRendererObj, mc.fontRendererObj.trimStringToWidth("[" + head.getSenderName() + "]", divSize2 - 10), this.xPosition + 5 + divSize1, this.yPosition + 5, j);
	            
	            //title
	            this.drawString(mc.fontRendererObj, 
	            		mc.fontRendererObj.trimStringToWidth((head.getTitle().equals("commands.moemail.SendMail.defaultTitle")? I18n.format("commands.moemail.SendMail.defaultTitle"): head.getTitle()), divSize3 - 10),
	            		this.xPosition + 5 + divSize1 + divSize2, this.yPosition + 5, j);
            }
        }
    }
}
