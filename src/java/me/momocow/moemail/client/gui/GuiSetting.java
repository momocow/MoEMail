package me.momocow.moemail.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import me.momocow.mobasic.client.gui.MoCenteredGuiScreen;
import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoButton;
import me.momocow.mobasic.client.gui.widget.MoIconButton;
import me.momocow.moemail.config.ConfigGeneral;
import me.momocow.moemail.init.ModConfigs;
import me.momocow.moemail.proxy.ClientProxy;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

public class GuiSetting extends MoCenteredGuiScreen
{
	private final static String NAME = "GuiSetting";
	private final static int MAX_SOUND_ID = 9;
	
	private final static ResourceLocation TEXTURE = new ResourceLocation("textures/gui/demo_background.png");
	private final static ResourceLocation HOMEBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/homebutton.png");
	private final static ResourceLocation BLANKBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/blankButton.png");
	private final static ResourceLocation PLAYBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/playButton.png");
	
	private GuiMailBox parent;
	private int currentSound = ModConfigs.general.mailNotificationSound;
	
	//gui
	private MoIconButton homeButton;
	private MoButton selectSound;
	private MoIconButton playSound;
	
	//text
	private String textTitle;
	private String textNotificationSound;
	
	public GuiSetting(GuiMailBox p) 
	{
		super(248, 166);
		this.setUnlocalizedName(Reference.MOD_ID + "." + NAME);
		
		this.textTitle = I18n.format(this.getUnlocalizedName() + ".title");
		this.textNotificationSound = I18n.format(this.getUnlocalizedName() + ".mailNotificationSound");
		
		this.parent = p;
	}
	
	@Override
	public void initGui() 
	{
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		
		this.homeButton = new MoIconButton(0, this.getGlobalX(220), this.row(2) - 5, 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, HOMEBUTTON);
		this.buttonList.add(this.homeButton);
		this.clearTooltip(this.homeButton.id);
		this.addTooltip(homeButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".home") + TextFormatting.YELLOW + "(Ctrl+H)");
		
		this.selectSound = new MoButton(1, this.col(9), this.row(3) - 4, 20, 20, "", BLANKBUTTON);
		this.setSound(this.currentSound);
		this.buttonList.add(this.selectSound);
		
		this.playSound = new MoIconButton(2, this.col(13), this.row(3) - 4, 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, PLAYBUTTON);
		this.buttonList.add(this.playSound);
		this.clearTooltip(this.playSound.id);
		this.addTooltip(playSound.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".playSound"));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		MoGuiScreen.drawProportionTexturedRect(TEXTURE, this.offsetX, this.offsetY, this.zLevel, 0, 0, 248, 166, 256, 256, this.guiWidth, this.guiHeight);
		
		this.drawButtonList(mouseX, mouseY);
		
		//gui title
		this.drawCenteredString(this.fontRendererObj, this.textTitle, this.getCenterX(), this.row(1), fontRendererObj.getColorCode('1'));
		
		//notitfication sound
		this.fontRendererObj.drawString(this.textNotificationSound, this.col(3), this.row(3), this.fontRendererObj.getColorCode('0'));
		
		//hovering text
    	if(this.homeButton.isHovered(mouseX, mouseY))
    	{
    		this.drawTooltip(this.homeButton.id, mouseX, mouseY);
    	}
    	else if(this.playSound.isHovered(mouseX, mouseY))
    	{
    		this.drawTooltip(this.playSound.id, mouseX, mouseY);
    	}
	}
	
	private void setSound(int newSound)
	{
		newSound = MathHelper.clamp_int(newSound, 0, MAX_SOUND_ID);
		
		this.currentSound = newSound;
		this.selectSound.displayString = TextFormatting.BOLD + (TextFormatting.ITALIC + String.valueOf(this.currentSound));
		
		char colorcode = '1';
		if(newSound < 6)
		{
			colorcode += newSound;
		}
		else if(newSound == 6)
		{
			colorcode = '9';
		}
		else
		{
			colorcode = (char) ('a' + 2 * (newSound - 7));
		}
		
		this.selectSound.packedFGColour = this.fontRendererObj.getColorCode(colorcode);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException 
	{
		super.keyTyped(typedChar, keyCode);
		
		if(keyCode == 35 && GuiScreen.isCtrlKeyDown())	//h
		{
			this.parent.displayGui();
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException 
	{		
		if(mouseButton == 2)
		{
			ClientProxy.playNotificationSound(this.currentSound);
			return;
		}
		
		for(GuiButton button: this.buttonList)
		{
			if(button.mousePressed(mc, mouseX, mouseY))
			{
				if(button.id == this.homeButton.id)
				{
					this.parent.displayGui();
				}
				else if(button.id == this.selectSound.id)
				{
					int newSound = this.currentSound;
					if(mouseButton == 0)
					{
						newSound ++;
					}
					else if(mouseButton == 1)
					{
						newSound --;
					}
					
					if(newSound < 0)
					{
						newSound = MAX_SOUND_ID;
					}
					else if(newSound > MAX_SOUND_ID)
					{
						newSound = 0;
					}
					
					this.setSound(newSound);
				}
				else if(button.id == this.playSound.id)
				{
					ClientProxy.playNotificationSound(this.currentSound);
				}
			}
		}
	}
	
	@Override
	public void onGuiClosed() 
	{
		Keyboard.enableRepeatEvents(false);
		
		ConfigGeneral conf = ModConfigs.general;
		ConfigElement configSound = new ConfigElement(conf.getConfig().get(conf.CATEGORY_GENERAL, "mailNotificationSound", conf.mailNotificationSound));
		configSound.set(Integer.valueOf(this.currentSound));
		
		ConfigChangedEvent event = new OnConfigChangedEvent(Reference.MOD_ID, null, mc.theWorld != null, false);
        MinecraftForge.EVENT_BUS.post(event);
	}
}
