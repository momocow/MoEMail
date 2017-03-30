package me.momocow.moemail.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import me.momocow.mobasic.client.gui.MoCenteredGuiScreen;
import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoIconButton;
import me.momocow.mobasic.client.gui.widget.MoTextArea;
import me.momocow.mobasic.client.gui.widget.MoTextArea.UpdatableGuiParent;
import me.momocow.mobasic.client.gui.widget.MoTextField;
import me.momocow.mobasic.client.gui.widget.MoVanillaScrollBar;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiNewMail extends MoCenteredGuiScreen implements UpdatableGuiParent<Integer>
{
	private static final int MAX_LINE = 12;
	private static final int MAX_TITLE_LEN = 30;
	private static final int MAX_CONTENT_LEN = 500;
	private final static int ENABLED_TEXT_COLOR = 5987163;
	
	private final static ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/mailbox.png");
	private final static ResourceLocation HOMEBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/homebutton.png");
	private final static ResourceLocation SCROLLBAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/scrollbar.png");
	private final static ResourceLocation TEXTFIELD = new ResourceLocation(Reference.MOD_ID, "textures/gui/textfield.png");
	private final static ResourceLocation SENDMAIL = new ResourceLocation(Reference.MOD_ID, "textures/gui/sendMail.png");
	private final static String NAME = "GuiNewMail";
	
	private GuiMailBox parent;
	
	private int pageCursor = 0;

	private String bufMailReceiver = "";
	private String bufMailTitle = "";
	private int stageCount = 1;
	private boolean forcedUpdate = true;
	private int contentLen = 0;
	
	//Gui
	private MoVanillaScrollBar  scrollbar;
	private MoIconButton homeButton;
	private MoIconButton sendMailButton;
	private MoTextField receiver;
	private MoTextField mailTitle;
	private MoTextArea mailContent;

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
		this.forceUpdate();
		
		this.mailContent = MoTextArea.load(new MoTextArea(this.getGlobalX(19), this.getGlobalY(43), 198, 9, MAX_LINE, fontRendererObj), this.mailContent);
		this.mailContent.setParent(this);
		this.mailContent.setDisplayStartLine(this.pageCursor);
		
		this.stageCount = this.mailContent.getLineCount() - MAX_LINE + 1;
		this.scrollbar = new MoVanillaScrollBar(this.getGlobalX(224), this.getGlobalY(42), this.zLevel, this.getGlobalY(152), 12, 15, this.stageCount, SCROLLBAR);
		this.scrollbar.setStage(this.pageCursor);
		
		this.homeButton = new MoIconButton(0, this.getGlobalX(220), this.row(2) - 5, 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, HOMEBUTTON);
		this.clearTooltip(this.homeButton.id);
		this.addTooltip(this.homeButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".home") + TextFormatting.YELLOW + "(Ctrl+H)");
		
		this.receiver = new MoTextField(1, this.fontRendererObj, this.col(7), this.row(2), 1, 11, 1, 1, 58, 8, 60, 20, 60, 10, TEXTFIELD);
		this.receiver.setVisible(true);
		this.receiver.setEnabled(true);
		this.receiver.setEnableBackgroundDrawing(true);
		this.receiver.setTextColor(ENABLED_TEXT_COLOR);
		this.receiver.setText(this.bufMailReceiver);
		this.receiver.setMaxStringLength(MAX_TITLE_LEN);
		
		this.mailTitle = new MoTextField(2, this.fontRendererObj, this.col(7), this.row(3) + 1, 1, 11, 1, 1, 58, 8, 60, 20, 100, 10, TEXTFIELD);
		this.mailTitle.setVisible(true);
		this.mailTitle.setEnabled(true);
		this.mailTitle.setEnableBackgroundDrawing(true);
		this.mailTitle.setTextColor(ENABLED_TEXT_COLOR);
		this.mailTitle.setText(this.bufMailTitle);
		this.mailTitle.setMaxStringLength(MAX_TITLE_LEN);
		
		this.sendMailButton = new MoIconButton(1, this.getGlobalX(195), this.row(2) - 5, 0, 64, 0, 0, 20, 20, 64, 64, 64, 128, SENDMAIL);
		this.clearTooltip(this.sendMailButton.id);
		this.addTooltip(this.sendMailButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".send")+ TextFormatting.YELLOW + "(Ctrl+Shift+S)");
	}
	
	@Override
	public void updateScreen() 
	{
		if(Math.max(this.mailContent.getLineCount() - MAX_LINE + 1, 1) != this.scrollbar.getStageNum())
		{
			this.initGui();
		}

		if(this.forcedUpdate || this.pageCursor != this.scrollbar.getStage())
		{
			this.pageCursor = this.scrollbar.getStage();
			this.mailContent.setDisplayStartLine(this.pageCursor);
			this.forcedUpdate = false;
		}
		
		this.mailContent.updateTextArea();
		this.contentLen = this.mailContent.getContentString().length();
	}
	
	public void forceUpdate()
	{
		this.forcedUpdate = true;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		MoGuiScreen.drawProportionTexturedRect(TEXTURE, this.offsetX, this.offsetY, this.zLevel, 0, 0, 248, 166, 256, 256, this.guiWidth, this.guiHeight);
		this.scrollbar.drawScrollBar();
		
		//gui title
		this.drawCenteredString(this.fontRendererObj, this.textGuiTitle, this.getCenterX(), this.row(1), this.fontRendererObj.getColorCode('1'));
    	
    	//receiver
		this.fontRendererObj.drawString(this.textReceiver + ": ", this.col(3), this.row(2), this.fontRendererObj.getColorCode('0'));
    	this.receiver.drawTextBox();
    	
    	//mail title
    	this.fontRendererObj.drawString(this.textMailTitle + ": ", this.col(3), this.row(3) + 1, this.fontRendererObj.getColorCode('0'));
    	this.mailTitle.drawTextBox();
    	
    	//mail content
    	this.mailContent.drawTextArea();
    	int remain = MAX_CONTENT_LEN - this.contentLen;
    	String textRemainWord = I18n.format(this.getUnlocalizedName() + ".remainWordCount", remain);
    	this.drawCenteredString(this.fontRendererObj, textRemainWord, this.getCenterX(), this.getGlobalY(151), this.fontRendererObj.getColorCode(remain==0? 'c': '8'), false);
    	
    	this.homeButton.drawButton(mc, mouseX, mouseY);
    	this.sendMailButton.drawButton(mc, mouseX, mouseY);
    	
    	//hovering text
    	if(this.homeButton.isHovered(mouseX, mouseY))
    	{
    		this.drawTooltip(this.homeButton.id, mouseX, mouseY);
    	}
    	else if(this.sendMailButton.isHovered(mouseX, mouseY))
    	{
    		this.drawTooltip(this.sendMailButton.id, mouseX, mouseY);
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
		
		if(this.contentLen < MAX_CONTENT_LEN || (!ChatAllowedCharacters.isAllowedCharacter(typedChar) && keyCode != 28 && keyCode != 156))
		{
			if(this.mailContent.keyTyped(typedChar, keyCode)) return;
		}

		if(keyCode == 35 && GuiScreen.isCtrlKeyDown())	//h
		{
			this.displayParentGui();
		}
		else if(keyCode == 31 && GuiScreen.isCtrlKeyDown() && GuiScreen.isShiftKeyDown())
		{
			this.changeGui(new GuiSendMailConfirm(this));
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
			else if(this.sendMailButton.mousePressed(mc, mouseX, mouseY))
			{
				this.sendMailButton.mouseClick(mc, mouseX, mouseY, mouseButton);
				this.changeGui(new GuiSendMailConfirm(this));
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
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) 
	{
		super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
		
		if(this.scrollbar.isDragged())
		{
			this.scrollbar.mouseClickMove(mouseX, mouseY);
		}
		
		this.mailContent.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
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
	public void onGuiClosed() 
	{
		Keyboard.enableRepeatEvents(false);
	}
	
	public String getReceiver()
	{
		String r = this.receiver.getText();
		return (r ==null)? "": r;
	}
	
	public String getTitle()
	{
		String t = this.mailTitle.getText();
		return (t == null)? "": t;
	}
	
	public String getContent()
	{
		return this.mailContent.getContentString();
	}

	/**
	 * info[0] is the new start line ID
	 */
	@Override
	public void onUpdate(Integer... info) 
	{
		if(info.length == 1)
		{
			if(info[0] >= 0 && info[0] < this.scrollbar.getStageNum())
			{
				this.scrollbar.setStage(info[0]);
			}
			else if(info[0] >= this.scrollbar.getStageNum())
			{
				this.pageCursor++;
				this.initGui();
			}
		}
	}
}