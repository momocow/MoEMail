package me.momocow.moemail.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.momocow.mobasic.client.gui.MoCenteredGuiScreen;
import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoIconButton;
import me.momocow.mobasic.client.gui.widget.MoVanillaScrollBar;
import me.momocow.moemail.init.ModChannels;
import me.momocow.moemail.init.ModConfigs;
import me.momocow.moemail.network.C2SFetchMailHeaderPacket;
import me.momocow.moemail.reference.Reference;
import me.momocow.moemail.server.MailPool.Mail.Header;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiMailBox extends MoCenteredGuiScreen
{
	public static final int PAGE_SIZE = 6;
	
	private final static ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/mailbox.png");
	private final static ResourceLocation ACCOUNTBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/personalInfoButton.png");
	private final static ResourceLocation NEWMAILBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/newMail.png");
	private final static ResourceLocation SETTINGBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/settingButton.png");
	private final static ResourceLocation SCROLLBAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/scrollbar.png");
	private final static String NAME = "GuiMailBox";
	
	private int pageCursor = 0;
	private int lastPageCount = 0;
	private int pageCount = 0;
	private boolean forceReload = true;
	
	//text
	private String textTitle;
	
	//Gui
	private MoVanillaScrollBar  scrollbar;
	private MoIconButton accountButton;
	private MoIconButton newMailButton;
	private MoIconButton settingButton;
	private List<GuiMailButton> mailButtonList = new ArrayList<GuiMailButton>();
	
	public GuiMailBox()
	{
		super(248, 166);
		this.setUnlocalizedName(Reference.MOD_ID + "." + NAME);
		
		this.textTitle = I18n.format(this.getUnlocalizedName() + ".title");
	}
	
	@Override
	public void initGui() 
	{		
		//MUST call super.initGui to draw the centeredScreen at the correct position
		super.initGui();

		this.scrollbar = new MoVanillaScrollBar(this.getGlobalX(224), this.getGlobalY(42), this.zLevel, this.getGlobalY(152), 12, 15, this.pageCount, this.pageCursor, SCROLLBAR);
		
		int initializedMail = this.mailButtonList.size();
		for(int i = 0; i< 6; i++)
		{
			if(i >= initializedMail)
			{
				GuiMailButton mail = new GuiMailButton(i, this.getGlobalX(19), this.getGlobalY(43 + 18 * i), 198, 18);
				mail.visible = true;
				mail.setUnread(false);
				this.mailButtonList.add(mail);
			}
			else
			{
				this.mailButtonList.get(i).setPosition(this.getGlobalX(19), this.getGlobalY(43 + 18 * i));
			}
		}
		
		if(!mc.isSingleplayer() || !ModConfigs.general.httpd.isDedicatedServerOnly)
		{
			this.accountButton = new MoIconButton(3, this.getGlobalX(220), this.row(2) - 5, 0, 64, 0, 0, 20, 20, 64, 64, 64, 128, ACCOUNTBUTTON);
			this.accountButton.visible = true;
			this.accountButton.enabled = true;
			this.clearTooltip(this.accountButton.id);
			this.addTooltip(accountButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".accountInfo") + TextFormatting.YELLOW + "(Ctrl+I)");
			this.buttonList.add(this.accountButton);
		}
		
		this.settingButton = new MoIconButton(2, this.getGlobalX(195), this.row(2) - 5, 0, 64, 0, 0, 20, 20, 64, 64, 64, 128, SETTINGBUTTON);
		this.settingButton.visible = true;
		this.settingButton.enabled = true;
		this.clearTooltip(this.settingButton.id);
		this.addTooltip(settingButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".setting") + TextFormatting.YELLOW + "(Ctrl+E)");
		this.buttonList.add(this.settingButton);
		
		this.newMailButton = new MoIconButton(1, this.getGlobalX(170), this.row(2) - 5, 0, 64, 0, 0, 20, 20, 64, 64, 64, 128, NEWMAILBUTTON);
		this.newMailButton.visible = true;
		this.newMailButton.enabled = true;
		this.clearTooltip(this.newMailButton.id);
		this.addTooltip(newMailButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".newMail") + TextFormatting.YELLOW + "(Ctrl+N)");
		this.buttonList.add(this.newMailButton);
	}
	
	@Override
	public void updateScreen() 
	{
		//here pageCursor means the page shown BEFORE update, it is used to check for page change
		if(this.forceReload || this.pageCursor != this.scrollbar.getStage())
		{
			ModChannels.mailSyncChannel.sendToServer(new C2SFetchMailHeaderPacket(mc.thePlayer.getUniqueID(), this.scrollbar.getStage()));
		}
		
		if(this.lastPageCount != this.pageCount)
		{
			this.initGui();
		}
		
		//update page cursor
		this.pageCursor = this.scrollbar.getStage();
		this.lastPageCount = this.pageCount;
		this.forceReload = false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		MoGuiScreen.drawProportionTexturedRect(TEXTURE, this.offsetX, this.offsetY, this.zLevel, 0, 0, 248, 166, 256, 256, this.guiWidth, this.guiHeight);
		this.scrollbar.drawScrollBar();
		
		//gui title
		this.drawCenteredString(fontRendererObj, this.textTitle, this.getCenterX(), this.row(1), fontRendererObj.getColorCode('1'));
		
		//default buttons
		this.drawButtonList(mouseX, mouseY);
		
		String textPageIndex = I18n.format(this.getUnlocalizedName() + ".pageIndex", (this.pageCount == 0)? 0: this.pageCursor + 1, this.pageCount);
		fontRendererObj.drawString(textPageIndex, this.getCenterX() - fontRendererObj.getStringWidth(textPageIndex) / 2, this.getGlobalY(153), fontRendererObj.getColorCode('8'));
		
		//mails
		for(GuiMailButton mbutton: this.mailButtonList)
		{
			mbutton.drawButton(mc, mouseX, mouseY);
		}
		
		//hovering text
		if(this.accountButton != null && this.accountButton.isHovered(mouseX, mouseY))
		{
			this.drawTooltip(this.accountButton.id, mouseX, mouseY);
		}
		else if(this.newMailButton != null && this.newMailButton.isHovered(mouseX, mouseY))
		{
			this.drawTooltip(this.newMailButton.id, mouseX, mouseY);
		}
		else if(this.settingButton != null && this.settingButton.isHovered(mouseX, mouseY))
		{
			this.drawTooltip(this.settingButton.id, mouseX, mouseY);
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		
		if(keyCode == 50)	//m
		{
			this.changeGui(null);
		}
		else if(keyCode == 200)	//key up
    	{
    		this.scrollbar.moveBackStage();
    	}
    	else if(keyCode == 208)	//key down
    	{
    		this.scrollbar.moveNextStage();
    	}
    	else if(keyCode == 49 && GuiScreen.isCtrlKeyDown())	//n
    	{
    		this.changeGui(new GuiNewMail(this));
    	}
    	else if(keyCode == 18 && GuiScreen.isCtrlKeyDown())	//e
    	{
    		this.changeGui(new GuiSetting(this));
    	}
    	else if(keyCode >= 2 && keyCode <= 7 && GuiScreen.isCtrlKeyDown()) //1~6
    	{
    		this.mailButtonList.get(keyCode - 2).displayMail(mc);
    	}
    	else if(keyCode >= 75 && keyCode <= 81 && keyCode != 78 && GuiScreen.isCtrlKeyDown())
    	{
    		int i = 0;
    		
    		if(keyCode >= 79 && keyCode <= 81)
    		{
    			i = keyCode - 79;
    		}
    		else
    		{
    			i = keyCode - 72;
    		}
    		
    		this.mailButtonList.get(i).displayMail(mc);
    	}
    	else if(this.accountButton != null && keyCode == 23 && GuiScreen.isCtrlKeyDown())
    	{
    		this.changeGui(new GuiMailBoxAccount(this));
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
					if(this.accountButton != null && button.id == this.accountButton.id)
					{
						this.changeGui(new GuiMailBoxAccount(this));
					}
					else if(button.id == this.newMailButton.id)
					{
						this.changeGui(new GuiNewMail(this));
					}
					else if(button.id == this.settingButton.id)
					{
						this.changeGui(new GuiSetting(this));
					}
				}
			}
			
			for(GuiMailButton mbutton: this.mailButtonList)
			{
				if(mbutton.mousePressed(mc, mouseX, mouseY))
				{
					mbutton.mouseClick(mc, mouseX, mouseY, mouseButton);
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
	
	public void updatePage(int mailCount, List<Header> headers)
	{
		this.pageCount = (int) Math.ceil((double) mailCount / (double)PAGE_SIZE);
	
		for(GuiMailButton button: this.mailButtonList)
		{
			if(button.id < headers.size())
			{
				Header head = headers.get(button.id);
				button.setMail(head);
			}
			else
			{
				button.setMail(null);
			}
		}
	}
	
	public void setForceReload()
	{
		this.forceReload = true;
	}
	
	public void displayGui()
	{
		this.forceReload = true;
		
		this.changeGui(this);
	}
}
