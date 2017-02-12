package me.momocow.moemail.client.gui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.momocow.mobasic.client.gui.MoCenteredGuiScreen;
import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoButton;
import me.momocow.mobasic.client.gui.widget.MoVanillaScrollBar;
import me.momocow.moemail.init.ModChannels;
import me.momocow.moemail.network.C2SFetchMailPacket;
import me.momocow.moemail.reference.Reference;
import me.momocow.moemail.server.MailPool.Mail;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiMailBox extends MoCenteredGuiScreen
{
	private static ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/mailbox.png");
	private final static ResourceLocation SCROLLBAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/scrollbar.png");
	private final static String NAME = "GuiMailBox";
	
	private int pageCursor = -1;
	private List<Mail> mails;
	private Set<UUID> unread;
	private URI WebURL;
	
	//text
	private String textTitle;
	private String textWebLink;
	
	//Gui
	private MoVanillaScrollBar  scrollbar;
	
	public GuiMailBox()
	{
		super(248, 166);
		this.setUnlocalizedName(Reference.MOD_ID + "." + NAME);
		
		this.textTitle = I18n.format(this.getUnlocalizedName() + ".title");
		this.textWebLink = I18n.format(this.getUnlocalizedName() + ".weblink");
		
		//ask server for web link
		//C2SFetchLinkPacket()
	}
	
	@Override
	public void initGui() 
	{
		//MUST call super.initGui to draw the centeredScreen at the correct position
		super.initGui();
		
		this.scrollbar = new MoVanillaScrollBar(this.getGlobalX(224), this.getGlobalY(42), this.zLevel, this.getGlobalY(152), 12, 15, 1, SCROLLBAR);
		
		for(int i = 0; i< 6; i++)
		{
			GuiMailButton mail = new GuiMailButton(i, this.getGlobalX(19), this.getGlobalY(43 + 18 * i), 198, 18);
			mail.visible = true;
			mail.setUnread(false);
			this.buttonList.add(mail);
		}
		
		this.buttonList.add(new GuiButton(6, this.getGlobalX(170), this.getGlobalY(17), 60, 20, this.textWebLink));
	}
	
	@Override
	public void updateScreen() 
	{
		//here pageCursor means the page shown BEFORE update, it is used to check for page change
		if(this.pageCursor != this.scrollbar.getStage())
		{
			ModChannels.mailSyncChannel.sendToServer(new C2SFetchMailPacket(mc.thePlayer.getUniqueID()));
		}
		
		//update page cursor
		this.pageCursor = this.scrollbar.getStage();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		MoGuiScreen.drawProportionTexturedRect(TEXTURE, this.offsetX, this.offsetY, this.zLevel, 0, 0, 248, 166, 256, 256, this.guiWidth, this.guiHeight);
		this.scrollbar.drawScrollBar();
		
		//title
		this.drawCenteredString(fontRendererObj, this.textTitle, this.getCenterX(), this.row(1), fontRendererObj.getColorCode('1'));
		
		//mails
		this.drawButtonList(mouseX, mouseY);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		
		if(keyCode == 50)	//m
		{
			this.changeGui(null);;
		}
		else if(keyCode == 200)	//key up
    	{
    		this.scrollbar.moveBackStage();
    	}
    	else if(keyCode == 208)	//key down
    	{
    		this.scrollbar.moveNextStage();
    	}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException 
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(mouseButton == 0)
		{
			for(GuiButton button: this.buttonList)
			{
				if(button.mousePressed(mc, mouseX, mouseY))
				{
					if(button instanceof MoButton)
					{
						((MoButton)button).mouseClick(mc, mouseX, mouseY, mouseButton);
					}
					else
					{
						if(button.id == 6)
						{
							this.openWebMailBox();
						}
					}
				}
			}
			
			if(this.scrollbar.isScrollBarClicked(mouseX, mouseY))
			{
				this.scrollbar.mouseClicked(mouseX, mouseY);
			}
			else if(this.scrollbar.isScrollFieldClicked(mouseX, mouseY))
			{
				this.scrollbar.scrollFieldClicked(mouseX, mouseY);
			}
		}
	}
	
	private void openWebMailBox() {
		if(this.WebURL != null)
		{
			this.openWebLink(this.WebURL);
		}
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		
		if(this.scrollbar.isDragged())
		{
			this.scrollbar.mouseClickMove(mouseX, mouseY);
		}
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		
		if(this.scrollbar.isDragged())
		{
			this.scrollbar.mouseReleased(mouseX, mouseY);
		}
	}
	
	@Override
    public void mouseWheelMove(int wheelMove) 
    {
		this.scrollbar.mouseWheelMove(wheelMove);
    }
}
