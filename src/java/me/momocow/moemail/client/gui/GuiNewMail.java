package me.momocow.moemail.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import me.momocow.mobasic.client.gui.MoCenteredGuiScreen;
import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoIconButton;
import me.momocow.mobasic.client.gui.widget.MoTextField;
import me.momocow.mobasic.client.gui.widget.MoVanillaScrollBar;
import me.momocow.moemail.client.gui.MoTextArea.CursorPos;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiNewMail extends MoCenteredGuiScreen
{
	private static final int MAX_LINE = 12;
	private static final int MAX_TITLE_LEN = 30;
	private final static int ENABLED_TEXT_COLOR = 5987163;
	
	private final static ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/mailbox.png");
	private final static ResourceLocation HOMEBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/homebutton.png");
	private final static ResourceLocation SCROLLBAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/scrollbar.png");
	private final static ResourceLocation TEXTFIELD = new ResourceLocation(Reference.MOD_ID, "textures/gui/textfield.png");
	private final static String NAME = "GuiNewMail";
	
	private GuiMailBox parent;
	
	private int pageCursor = 0;

	private String bufMailReceiver = "";
	private String bufMailTitle = "";
	private int stageCount = 1;
	private boolean isTextAreaFocused = false;
	private List<String> bufMailContent = new ArrayList<String>();
	
	//Gui
	private MoVanillaScrollBar  scrollbar;
	private MoIconButton homeButton;
	private MoTextField receiver;
	private MoTextField mailTitle;
	private MoTextArea mailContent;
	private CursorPos textareaCursor = null;

	//text
	private String textGuiTitle;
	private String textReceiver;
	private String textMailTitle;
	
	public GuiNewMail(GuiMailBox p)
	{
		super(248, 166);
		this.setUnlocalizedName(Reference.MOD_ID + "." + NAME);
		
		this.parent = p;
		
		this.textGuiTitle = I18n.format(this.getUnlocalizedName() + ".title");
		this.textReceiver = I18n.format(this.getUnlocalizedName() + ".receiver");
		this.textMailTitle = I18n.format(this.getUnlocalizedName() + ".mailTitle");
	}
	
	@Override
	public void initGui() 
	{
		//MUST call super.initGui to draw the centeredScreen at the correct position
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		
		this.mailContent = new MoTextArea(this.getGlobalX(19), this.getGlobalY(43), 198, 9, MAX_LINE, this.fontRendererObj);
		this.mailContent.setRawLines(this.bufMailContent);
		if(this.textareaCursor != null) this.mailContent.setCursorPos(this.textareaCursor);
		this.mailContent.setFocused(this.isTextAreaFocused);
		
		this.stageCount = this.mailContent.getLineCount() - MAX_LINE + 1;
		if(this.stageCount < 1) this.stageCount = 1;
		this.scrollbar = new MoVanillaScrollBar(this.getGlobalX(224), this.getGlobalY(42), this.zLevel, this.getGlobalY(152), 12, 15, this.stageCount, SCROLLBAR);
		this.scrollbar.setStage(this.pageCursor);
		
		this.homeButton = new MoIconButton(0, this.getGlobalX(220), this.row(2) - 5, 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, HOMEBUTTON);
		this.clearTooltip(this.homeButton.id);
		this.addTooltip(homeButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".home"));
		
		this.receiver = new MoTextField(1, this.fontRendererObj, this.col(7), this.row(2), 1, 11, 1, 1, 58, 8, 60, 20, 60, 10, TEXTFIELD);
		this.receiver.setVisible(true);
		this.receiver.setEnabled(true);
		this.receiver.setEnableBackgroundDrawing(true);
		this.receiver.setTextColor(ENABLED_TEXT_COLOR);
		this.receiver.setText(this.bufMailReceiver);
		this.receiver.setMaxStringLength(MAX_TITLE_LEN);
		
		this.mailTitle = new MoTextField(2, this.fontRendererObj, this.col(7), this.row(3) + 2, 1, 11, 1, 1, 58, 8, 60, 20, 100, 10, TEXTFIELD);
		this.mailTitle.setVisible(true);
		this.mailTitle.setEnabled(true);
		this.mailTitle.setEnableBackgroundDrawing(true);
		this.mailTitle.setTextColor(ENABLED_TEXT_COLOR);
		this.mailTitle.setText(this.bufMailTitle);
		this.mailTitle.setMaxStringLength(MAX_TITLE_LEN);
	}
	
	@Override
	public void updateScreen() 
	{
		this.mailContent.updateTextArea(this.pageCursor = this.scrollbar.getStage());
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		MoGuiScreen.drawProportionTexturedRect(TEXTURE, this.offsetX, this.offsetY, this.zLevel, 0, 0, 248, 166, 256, 256, this.guiWidth, this.guiHeight);
		this.scrollbar.drawScrollBar();
		
		//gui title
		this.drawCenteredString(fontRendererObj, this.textGuiTitle, this.getCenterX(), this.row(1), fontRendererObj.getColorCode('1'));
    	
    	//receiver
    	fontRendererObj.drawString(this.textReceiver + ": ", this.col(3), this.row(2), fontRendererObj.getColorCode('0'));
    	this.receiver.drawTextBox();
    	
    	//mail title
    	fontRendererObj.drawString(this.textMailTitle + ": ", this.col(3), this.row(3) + 2, fontRendererObj.getColorCode('0'));
    	this.mailTitle.drawTextBox();
    	
    	//mail content
    	this.mailContent.drawTextArea();
    	
    	this.homeButton.drawButton(mc, mouseX, mouseY);
    	
    	//hovering text
    	if(this.homeButton.isHovered(mouseX, mouseY))
    	{
    		this.drawTooltip(this.homeButton.id, mouseX, mouseY);
    	}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException 
	{
		if(this.receiver.textboxKeyTyped(typedChar, keyCode))
		{
			this.bufMailReceiver = this.receiver.getText();
			return;
		}
		
		if(this.mailTitle.textboxKeyTyped(typedChar, keyCode))
		{
			this.bufMailTitle = this.mailTitle.getText();
			return;
		}
		
		if(this.mailContent.keyTyped(typedChar, keyCode))
		{
			this.bufMailContent = this.mailContent.getRawLines();
			this.scrollbar.setStage(this.pageCursor = this.mailContent.getCurrentLiineStart());
			
			int c = this.mailContent.getLineCount() - MAX_LINE + 1;
			if(c < 1) c = 1;
			if(c != this.stageCount)
			{
				this.isTextAreaFocused = this.mailContent.isFocused();
				this.textareaCursor = this.mailContent.getCursorPos();
				this.initGui();
			}
			return;
		}

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
		
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException 
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		this.receiver.mouseClicked(mouseX, mouseY, mouseButton);
		this.mailTitle.mouseClicked(mouseX, mouseY, mouseButton);
		this.mailContent.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(mouseButton == 0)
		{
			if(this.homeButton.mousePressed(mc, mouseX, mouseY))
			{
				this.homeButton.mouseClick(mc, mouseX, mouseY, mouseButton);
				this.displayParentGui();
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
	
	public void displayParentGui()
	{
		this.parent.displayGui();
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
}