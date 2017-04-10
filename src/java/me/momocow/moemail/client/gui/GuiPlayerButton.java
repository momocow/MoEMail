package me.momocow.moemail.client.gui;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoButton;
import me.momocow.mobasic.proxy.Client;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Make original boolean 'enabled' to a new usage, 'isMailUnread'; true means the mail is unread, otherwise, false;
 * @author MomoCow
 *
 */
public class GuiPlayerButton extends MoButton
{
	private static final int MAX_NAME_LEN_PIXEL= 44;
	
	private boolean isFocused = false;
	private GameProfile player;

	public GuiPlayerButton(int buttonId, int x, int y, int widthIn, int heightIn) 
	{
		super(buttonId, x, y, widthIn, heightIn, "", new ResourceLocation(Reference.MOD_ID, "textures/gui/mailbutton.png"));
	}
	
	public void setPlayer(GameProfile profile)
	{
		if(profile == null)
		{
			this.displayString = "";
			this.player = null;
			return;
		}
		
		FontRenderer fontObj = Minecraft.getMinecraft().fontRendererObj;
		this.displayString = fontObj.getStringWidth(profile.getName()) > MAX_NAME_LEN_PIXEL?
				fontObj.trimStringToWidth(profile.getName(), MAX_NAME_LEN_PIXEL - 6) + "...": profile.getName();
		this.player = profile;
	}
	
	public boolean hasPlayer()
	{
		return this.player != null;
	}
	
	public GameProfile getPlayerProfile()
	{
		return this.player;
	}
	
	public UUID getPlayerId()
	{
		return (this.hasPlayer())? this.player.getId(): null;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) 
	{
		return this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}
	
	//including mousePressed check
	@Override
	public boolean mouseClick(Minecraft mc, int mouseX, int mouseY, int mouseButton) {
		if(this.hasPlayer() && super.mouseClick(mc, mouseX, mouseY, mouseButton))
		{
			return true;
		}
		return false;
	}
	
	public void setFocused(boolean focus)
	{
		this.isFocused = focus;
	}
	
	public boolean getFocused()
	{
		return this.isFocused;
	}
	
	public void toggleFocused()
	{
		this.isFocused = !this.isFocused;
	}
	
	@Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    	if (this.visible)
        {
    		GlStateManager.pushMatrix();
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            MoGuiScreen.drawProportionTexturedRect(this.TEXTURE, this.xPosition, this.yPosition, this.zLevel, 0, (this.hasPlayer() && this.hovered)? 18: 0, this.width, this.height, 198, 36, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (this.hovered)
            {
                j = 16777120;
            }
            
            if(this.hasPlayer())
            {
            	this.drawString(mc.fontRendererObj, this.displayString, this.xPosition + 22, this.yPosition + 4, j);
            
            	GlStateManager.disableDepth();
                GlStateManager.disableAlpha();
            	Gui.drawRect(this.xPosition + 3, this.yPosition + 1, this.xPosition + 19, this.yPosition + 17, -1);
                GlStateManager.enableAlpha();
                GlStateManager.enableDepth();
                
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            	ResourceLocation skin = Client.getPlayerSkin(this.player);
				mc.getTextureManager().bindTexture(skin);
				Gui.drawScaledCustomSizeModalRect(this.xPosition + 3, this.yPosition + 1, 8, 8, 8, 8, 16, 16, 64, 64);
				GlStateManager.popMatrix();
            }
            
            if(this.isFocused )
            {
            	GlStateManager.disableDepth();
            	Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 1140850943);
                GlStateManager.enableDepth();
            }
            GlStateManager.popMatrix();
        }
    }
}
