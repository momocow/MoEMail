package me.momocow.moemail.client.gui;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import me.momocow.mobasic.client.gui.MoCenteredGuiScreen;
import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoIconButton;
import me.momocow.mobasic.client.gui.widget.MoTextFieldPassword;
import me.momocow.moemail.MoEMail;
import me.momocow.moemail.init.ModChannels;
import me.momocow.moemail.network.C2SCheckAccountPacket;
import me.momocow.moemail.network.C2SFetchMailBoxURLPacket;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiMailBoxAccount extends MoCenteredGuiScreen
{
	private final static int MAX_PASSWD_LEN = 20; 
	private final static int ENABLED_TEXT_COLOR = 5987163; 
	private final static ResourceLocation TEXTURE = new ResourceLocation("textures/gui/demo_background.png");
	private final static ResourceLocation HOMEBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/homebutton.png");
	private final static ResourceLocation PASSTEXT = new ResourceLocation(Reference.MOD_ID, "textures/gui/textfield.png");
	private final static String NAME = "GuiMailBoxAccount";
	
	private final GuiMailBox parent;
	
	private URI WebURL = null;
	private boolean showWarnAccountNotInit = true;
	private boolean showWarnMaxPasswd = false;
	private boolean showWarnPasswdCheckFail = false;
	
	//text
	private String textWebLink;
	private String textTitle;
	private String textAccount;
	private String textWarnAccountNotInit;
	private String textWarnMaxPasswd;
	private String textWarnPasswdCheckFail;
	private String textOldPasswd;
	private String textNewPasswd;
	private String textCheckedPasswd;
	private String textSetPasswd;
	
	//input
	private Map<String, String> inputs = new HashMap<String, String>();
	
	//gui
	private MoIconButton homeButton;
	private GuiButton linkButton;
	private MoTextFieldPassword oldPasswd;
	private MoTextFieldPassword newPasswd;
	private MoTextFieldPassword checkedPasswd;
	private GuiButton setPasswd;
	private Map<String, MoTextFieldPassword> passwds = new HashMap<String, MoTextFieldPassword>();
	
	public GuiMailBoxAccount(GuiMailBox p) 
	{
		super(248, 166);
		this.setUnlocalizedName(Reference.MOD_ID + "." + NAME);
		
		this.parent = p;
		this.textWebLink = "(" + I18n.format(this.getUnlocalizedName() + ".linkFail") + ")";
		this.textTitle = I18n.format(this.getUnlocalizedName() + ".title");
		this.textAccount = I18n.format(this.getUnlocalizedName() + ".account");
		this.textWarnAccountNotInit = I18n.format(this.getUnlocalizedName() + ".warn.accountNotInitialized");
		this.textWarnMaxPasswd = I18n.format(this.getUnlocalizedName() + ".warn.maxPasswd", MAX_PASSWD_LEN);
		this.textWarnPasswdCheckFail = I18n.format(this.getUnlocalizedName() + ".warn.passwdCheckFail");
		this.textOldPasswd =  I18n.format(this.getUnlocalizedName() + ".oldPasswd");
		this.textNewPasswd =  I18n.format(this.getUnlocalizedName() + ".newPasswd");
		this.textCheckedPasswd =  I18n.format(this.getUnlocalizedName() + ".checkedPasswd");
		this.textSetPasswd =  I18n.format(this.getUnlocalizedName() + ".setPasswd");
		
		this.inputs.put(this.textOldPasswd, "");
		this.inputs.put(this.textNewPasswd, "");
		this.inputs.put(this.textCheckedPasswd, "");
	}
	
	@Override
	public void initGui() 
	{
		super.initGui();
		Keyboard.enableRepeatEvents(true);
				
		this.homeButton = new MoIconButton(0, this.getGlobalX(220), this.row(2) - 5, 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, HOMEBUTTON);
		this.buttonList.add(this.homeButton);
		this.clearTooltip(this.homeButton.id);
		this.addTooltip(homeButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".home"));
		
		this.linkButton = new GuiButton(1, this.getCenterX() - 30, this.getGlobalY(139), 60, 20, this.textWebLink);
		this.linkButton.visible = true;
		this.linkButton.enabled = false;
		this.buttonList.add(this.linkButton);
		
		this.oldPasswd = new MoTextFieldPassword(2, this.fontRendererObj, this.col(7), this.row(4), 0, 10, 0, 0, 60, 10, 60, 20, 90, 10, PASSTEXT);
		this.oldPasswd.setVisible(true);
		this.oldPasswd.setEnabled(false);
		this.oldPasswd.setEnableBackgroundDrawing(true);
		this.oldPasswd.setText(this.inputs.get(this.textOldPasswd));
		this.oldPasswd.setTextColor(ENABLED_TEXT_COLOR);
		this.oldPasswd.setMaxStringLength(MAX_PASSWD_LEN);
		this.passwds.put(this.textOldPasswd, this.oldPasswd);
		
		this.newPasswd = new MoTextFieldPassword(3, this.fontRendererObj, this.col(7), this.row(4) + 12, 0, 10, 0, 0, 60, 10, 60, 20, 90, 10, PASSTEXT);
		this.newPasswd.setVisible(true);
		this.newPasswd.setEnabled(true);
		this.newPasswd.setText(this.inputs.get(this.textNewPasswd));
		this.newPasswd.setEnableBackgroundDrawing(true);
		this.newPasswd.setTextColor(ENABLED_TEXT_COLOR);
		this.newPasswd.setMaxStringLength(MAX_PASSWD_LEN);
		this.passwds.put(this.textNewPasswd, this.newPasswd);
		
		this.checkedPasswd = new MoTextFieldPassword(4, this.fontRendererObj, this.col(7), this.row(4) + 24, 0, 10, 0, 0, 60, 10, 60, 20, 90, 10, PASSTEXT);
		this.checkedPasswd.setVisible(true);
		this.checkedPasswd.setEnabled(true);
		this.checkedPasswd.setText(this.inputs.get(this.textCheckedPasswd));
		this.checkedPasswd.setEnableBackgroundDrawing(true);
		this.checkedPasswd.setTextColor(ENABLED_TEXT_COLOR);
		this.checkedPasswd.setMaxStringLength(MAX_PASSWD_LEN);
		this.passwds.put(this.textCheckedPasswd, this.checkedPasswd);
		
		this.setPasswd = new GuiButton(5, this.col(7), this.row(4) + 36, 45, 10, this.textSetPasswd);
		this.setPasswd.visible = true;
		this.setPasswd.enabled = true;
		this.buttonList.add(this.setPasswd);
		
		//get URL
		ModChannels.httpdChannel.sendToServer(new C2SFetchMailBoxURLPacket());
		this.checkAccount();
	}
	
	@Override
	public void updateScreen() 
	{
		if(!this.linkButton.enabled && !this.showWarnAccountNotInit && this.WebURL != null)
		{
			this.linkButton.enabled = true;
			this.oldPasswd.setEnabled(true);
		}
		
		if(this.newPasswd.getText().length() >= MAX_PASSWD_LEN)
		{
			this.showWarnMaxPasswd = true;
		}
		else
		{
			this.showWarnMaxPasswd = false;
		}
		
		if(!this.checkedPasswd.isFocused() && !this.checkedPasswd.getText().isEmpty() && !this.checkedPasswd.getText().equals(this.newPasswd.getText()))
		{
			this.showWarnPasswdCheckFail = true;
		}
		else
		{
			this.showWarnPasswdCheckFail = false;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		MoGuiScreen.drawProportionTexturedRect(TEXTURE, this.offsetX, this.offsetY, this.zLevel, 0, 0, 248, 166, 256, 256, this.guiWidth, this.guiHeight);
		
		this.drawButtonList(mouseX, mouseY);
		
		//gui title
		this.drawCenteredString(this.fontRendererObj, this.textTitle, this.getCenterX(), this.row(1), fontRendererObj.getColorCode('1'));
		
		//id
		fontRendererObj.drawString(this.textAccount+ ": ", this.col(7) - 5 - fontRendererObj.getStringWidth(this.textAccount+ ": "), this.row(3), fontRendererObj.getColorCode('0'));
    	fontRendererObj.drawString(mc.thePlayer.getName(), this.col(7), this.row(3), fontRendererObj.getColorCode('8'));
    	
    	//passwd
    	for(Entry<String, MoTextFieldPassword> pwd: this.passwds.entrySet())
    	{
    		fontRendererObj.drawString(pwd.getKey() + ": ", this.col(7) - 5 - fontRendererObj.getStringWidth(pwd.getKey() + ": "), pwd.getValue().yPosition, fontRendererObj.getColorCode('0'));
    		pwd.getValue().drawTextBox();
    	}

    	//warn message
    	if(this.showWarnAccountNotInit)
    	{
    		fontRendererObj.drawString(this.textWarnAccountNotInit, this.getCenterX() - fontRendererObj.getStringWidth(this.textWarnAccountNotInit) / 2, this.getGlobalY(129), 
    				fontRendererObj.getColorCode('c'));
    	}
    	
    	if(this.showWarnMaxPasswd)
    	{
    		fontRendererObj.drawString(this.textWarnMaxPasswd, this.col(7) + 93, this.row(4) + 12, fontRendererObj.getColorCode('c'));
    	}
    	
    	if(this.showWarnPasswdCheckFail)
    	{
    		fontRendererObj.drawString(this.textWarnPasswdCheckFail, this.col(7) + 93, this.row(4) + 24, fontRendererObj.getColorCode('c'));
    	}
    	
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
		
		for(Entry<String, MoTextFieldPassword> pwd: this.passwds.entrySet())
		{
			if(pwd.getValue().textboxKeyTyped(typedChar, keyCode))
			{
				this.inputs.put(pwd.getKey(), pwd.getValue().getText());
				return;
			}
		}
		
		if(keyCode == 50)	//m
		{
			this.changeGui(null);;
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException 
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		for(MoTextFieldPassword pwd: this.passwds.values())
		{
			pwd.mouseClicked(mouseX, mouseY, mouseButton);
		}
		
		if(mouseButton == 0)
		{
			if(this.homeButton.mousePressed(mc, mouseX, mouseY))
			{
				this.parent.displayGui();
			}
			else if(this.linkButton.mousePressed(mc, mouseX, mouseY))
			{
				this.openWebMailBox();
			}
			else if(this.setPasswd.mousePressed(mc, mouseX, mouseY))
			{
				if(this.checkForm())
				{
					this.setPasswdToHttpd();
				}
			}
		}
	}
	
	private boolean checkForm()
	{
		return !this.showWarnPasswdCheckFail && (this.showWarnAccountNotInit || );
	}
	
	private void checkAccount()
	{
		ModChannels.httpdChannel.sendToServer(new C2SCheckAccountPacket(mc.thePlayer.getUniqueID()));
	}
	
	public void updateAccountStatus(boolean status)
	{
		this.showWarnAccountNotInit = !status;
	}
	
	private void openWebMailBox() {
		if(this.WebURL != null)
		{
			this.openWebLink(this.WebURL);
		}
	}
	
	private void setPasswdToHttpd()
	{
		
	}
	
	public void updateWebLink(String url)
	{
		try {
			this.WebURL = new URI(url);
		} catch (Exception e) 
		{
			MoEMail.logger.warn("Fail to create the link to the Web MailBox", e);
		}
		
		if(this.WebURL != null)
		{
			this.linkButton.displayString = I18n.format(this.getUnlocalizedName() + ".weblink");
		}
	}
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}
}
