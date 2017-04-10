package me.momocow.moemail.client.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.momocow.mobasic.client.gui.MoCenteredGuiScreen;
import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoIconButton;
import me.momocow.mobasic.client.gui.widget.MoVanillaScrollBar;
import me.momocow.moemail.init.ModChannels;
import me.momocow.moemail.network.C2SFetchMailContentPacket;
import me.momocow.moemail.reference.Reference;
import me.momocow.moemail.server.MailPool.Mail.Header;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiMail extends MoCenteredGuiScreen
{
	public static final int MAX_LINE = 12;
	
	private final static ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/mailbox.png");
	private final static ResourceLocation HOMEBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/homebutton.png");
	private final static ResourceLocation SCROLLBAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/scrollbar.png");
	private final static ResourceLocation DELETEBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/deleteButton.png");
	private final static String NAME = "GuiMail";
	
	private DateFormat df = new SimpleDateFormat("MM.dd HH:mm");
	private GuiMailBox parent;
	private Header header;
	private int pageCursor = 0;
	private int pageCount = 1;
	private List<String> mailContent = new ArrayList<String>();
	
	private String textGuiTitle;
	private String textTimestamp;
	private String textSender;
	private String textMailTitle;
	
	//Gui
	private MoVanillaScrollBar  scrollbar;
	private MoIconButton homeButton;
	private MoIconButton deleteButton;
	
	public GuiMail(GuiMailBox p, Header h)
	{
		super(248, 166);
		this.setUnlocalizedName(Reference.MOD_ID + "." + NAME);
		
		this.parent = p;
		this.header = h;
		
		this.textGuiTitle = I18n.format(this.getUnlocalizedName() + ".title");
		this.textTimestamp = I18n.format(this.getUnlocalizedName() + ".time");
		this.textSender = I18n.format(this.getUnlocalizedName() + ".sender");
		this.textMailTitle = I18n.format(this.getUnlocalizedName() + ".mailTitle");
		this.mailContent.add("(" + I18n.format(this.getUnlocalizedName() + ".loading") + "... )");
		
		//fetch mail
		ModChannels.mailSyncChannel.sendToServer(new C2SFetchMailContentPacket(this.header.getId()));
	}
	
	@Override
	public void initGui() 
	{
		//MUST call super.initGui to draw the centeredScreen at the correct position
		super.initGui();
		
		this.scrollbar = new MoVanillaScrollBar(this.getGlobalX(224), this.getGlobalY(42), this.zLevel, this.getGlobalY(152), 12, 15, this.pageCount, SCROLLBAR);
		this.homeButton = new MoIconButton(0, this.getGlobalX(220), this.row(2) - 5, 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, HOMEBUTTON);
		this.deleteButton = new MoIconButton(1, this.getGlobalX(195), this.row(2) - 5, 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, DELETEBUTTON);
		this.clearTooltip(this.homeButton.id);
		this.addTooltip(homeButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".home") + TextFormatting.YELLOW + "(Ctrl+H)");
	}
	
	@Override
	public void updateScreen() 
	{
		this.pageCursor = this.scrollbar.getStage();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		MoGuiScreen.drawProportionTexturedRect(TEXTURE, this.offsetX, this.offsetY, this.zLevel, 0, 0, 248, 166, 256, 256, this.guiWidth, this.guiHeight);
		this.scrollbar.drawScrollBar();
		
		//gui title
		this.drawCenteredString(fontRendererObj, this.textGuiTitle, this.getCenterX(), this.row(1), fontRendererObj.getColorCode('1'));
		
		//time
    	int stop = fontRendererObj.drawString(this.textTimestamp + ": ", this.col(3), this.row(2), fontRendererObj.getColorCode('0'));
    	fontRendererObj.drawString(df.format(this.header.getTimestamp()), stop, this.row(2), fontRendererObj.getColorCode('8'));
    	
    	//sender
    	stop = fontRendererObj.drawString(this.textSender + ": ", this.getCenterX(), this.row(2), fontRendererObj.getColorCode('0'));
    	fontRendererObj.drawString(this.header.getSenderName(), stop, this.row(2), fontRendererObj.getColorCode('8'));
    	
    	//mail title
    	stop = fontRendererObj.drawString(this.textMailTitle + ": ", this.col(3), this.row(3), fontRendererObj.getColorCode('0'));
    	fontRendererObj.drawString(this.header.getTitle().equals("gui.moemail.mail.defaultTitle")? I18n.format("gui.moemail.mail.defaultTitle"): this.header.getTitle(), 
    			stop, this.row(3), fontRendererObj.getColorCode('8'));
    	    	
    	//mail content
    	for(int i = 0; i< MAX_LINE; i++)
    	{
    		if(i + this.pageCursor < this.mailContent.size())
    		{
    			this.fontRendererObj.drawString(this.mailContent.get(i + this.pageCursor), 
    					this.getGlobalX(24), this.getGlobalY(43 + 9 * i), fontRendererObj.getColorCode('8'));
    		}
    		else
    		{
    			break;
    		}
    	}
    	
    	this.homeButton.drawButton(mc, mouseX, mouseY);
    	this.deleteButton.drawButton(mc, mouseX, mouseY);
    	
    	//hovering text
    	if(this.homeButton.isHovered(mouseX, mouseY))
    	{
    		this.drawTooltip(this.homeButton.id, mouseX, mouseY);
    	}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException 
	{
		super.keyTyped(typedChar, keyCode);
		
		if(keyCode == 35 && GuiScreen.isCtrlKeyDown())	//h
		{
			this.displayParentGui();
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
			if(this.homeButton.mousePressed(mc, mouseX, mouseY))
			{
				this.homeButton.mouseClick(mc, mouseX, mouseY, mouseButton);
				this.displayParentGui();
				return;
			}
			else if(this.deleteButton.mousePressed(mc, mouseX, mouseY))
			{
				this.changeGui(new GuiDeleteMailConfirm(this));
				return;
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
	
	public void updateContent(String rawContent)
	{
		this.mailContent = this.fontRendererObj.listFormattedStringToWidth(rawContent, 188);
		this.pageCount = Math.max(this.mailContent.size() - MAX_LINE + 1, 1);
		this.initGui();
	}
	
	public void displayParentGui()
	{
		this.parent.displayGui();
	}
	
	public UUID getMailId()
	{
		return this.header.getId();
	}
}
