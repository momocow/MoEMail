package me.momocow.moemail.client.gui;

import java.io.IOException;

import me.momocow.mobasic.client.gui.MoCenteredGuiScreen;
import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoIconButton;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiSendMailConfirm extends MoCenteredGuiScreen
{
	private final static ResourceLocation TEXTURE = new ResourceLocation("textures/gui/demo_background.png");
	private final static ResourceLocation OK = new ResourceLocation(Reference.MOD_ID, "textures/gui/okButton.png");
	private final static ResourceLocation CANCEL = new ResourceLocation(Reference.MOD_ID, "textures/gui/cancelButton.png");
	private final static String NAME = "GuiSendMailConfirm";

	private final GuiNewMail parent;
	
	//text
	private String textTitle;
	
	//gui
	private MoIconButton okButton;
	private MoIconButton cancelButton;

	public GuiSendMailConfirm(GuiNewMail p) 
	{
		super(248, 166);
		this.setUnlocalizedName(Reference.MOD_ID + "." + NAME);
		
		this.parent = p;
		
		this.textTitle =  I18n.format(this.getUnlocalizedName() + ".title");
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		this.okButton = new MoIconButton(0, this.getCenterX() - 30, this.row(4), 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, OK);
		this.buttonList.add(this.okButton);
		this.cancelButton = new MoIconButton(1, this.getCenterX() + 10, this.row(4), 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, CANCEL);
		this.buttonList.add(this.cancelButton);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		//bg texture
		MoGuiScreen.drawProportionTexturedRect(TEXTURE, this.offsetX, this.offsetY, this.zLevel, 0, 0, 248, 166, 256, 256, this.guiWidth, this.guiHeight);
		
		//title
		this.drawCenteredString(this.fontRendererObj, this.textTitle, this.getCenterX(), this.row(1), fontRendererObj.getColorCode('1'));
		
		for(GuiButton button: this.buttonList)
		{
			button.drawButton(mc, mouseX, mouseY);
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException 
	{
		super.keyTyped(typedChar, keyCode);
		
		if(keyCode == 50)	//m
		{
			this.changeGui(this.parent);
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
				//ok
				if(button.id == 0 && button.mousePressed(mc, mouseX, mouseY))
				{
					
				}
				//cancel
				else if(button.id == 1 && button.mousePressed(mc, mouseX, mouseY))
				{
					this.changeGui(this.parent);
				}
			}
		}
	}
	
	public void onMailDeleted()
	{
		this.parent.displayParentGui();
	}
}
