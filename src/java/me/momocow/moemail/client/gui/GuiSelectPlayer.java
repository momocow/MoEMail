package me.momocow.moemail.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import me.momocow.mobasic.client.gui.MoCenteredGuiScreen;
import me.momocow.mobasic.client.gui.MoGuiScreen;
import me.momocow.mobasic.client.gui.widget.MoIconButton;
import me.momocow.mobasic.client.gui.widget.MoTextField;
import me.momocow.mobasic.client.gui.widget.MoVanillaScrollBar;
import me.momocow.moemail.reference.Reference;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiSelectPlayer extends MoCenteredGuiScreen
{
	private static final String NAME = "GuiSelectPlayer";
	private final static ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/mailbox.png");
	private final static ResourceLocation SCROLLBAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/scrollbar.png");
	private final static ResourceLocation TEXTFIELD = new ResourceLocation(Reference.MOD_ID, "textures/gui/textfield.png");
	private final static ResourceLocation OKBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/okButton.png");
	private final static ResourceLocation CANCELBUTTON = new ResourceLocation(Reference.MOD_ID, "textures/gui/cancelButton.png");

	private final static int PLAYER_IN_ROW = 3;
	private final static int PLAYER_BUTTON_WIDTH = 66;
	private final static int PLAYER_BUTTON_HEIGHT = 18;
	private final static int MAX_NAME_LEN = 50;
	private final static int MAX_PLAYER_PER_PAGE = 18;
	private final static int ENABLED_TEXT_COLOR = 5987163;
	
	private GuiNewMail parent;
	
	private String textGuiTitle;
	private String textSearchbar;
	
	private List<GuiPlayerButton> playerButtons = new ArrayList<GuiPlayerButton>();
	private List<GameProfile> playerList = new ArrayList<GameProfile>();
	private List<GameProfile> filteredPlayerList = new ArrayList<GameProfile>();
	private int pageCursor = 0;
	private boolean isDirty = true;
	private GameProfile currentFocused;
	
	private MoVanillaScrollBar  scrollbar;
	private MoTextField searchbar;
	private MoIconButton okButton;
	private MoIconButton cancelButton;
	
	public GuiSelectPlayer(GuiNewMail p, List<GameProfile> players)
	{
		super(248, 166);
		this.setUnlocalizedName(Reference.MOD_ID + "." + NAME);
		
		this.parent = p;
		
		for(GameProfile profile: players)
		{
			if(!profile.isComplete())
			{
				profile = TileEntitySkull.updateGameprofile(profile);
			}
			
			this.playerList.add(profile);
		}
		this.filteredPlayerList = Lists.newArrayList(this.playerList);
		
		this.textGuiTitle = I18n.format(this.getUnlocalizedName() + ".title");
		this.textSearchbar = I18n.format(this.getUnlocalizedName() + ".searchbar");
	}
	
	@Override
	public void initGui() 
	{
		//MUST call super.initGui to draw the centeredScreen at the correct position
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		
		int stageCount = (int) Math.ceil((float) filteredPlayerList.size() / (float)MAX_PLAYER_PER_PAGE);
		this.scrollbar = new MoVanillaScrollBar(this.getGlobalX(224), this.getGlobalY(42), this.zLevel, this.getGlobalY(152), 12, 15, stageCount, SCROLLBAR);
		this.scrollbar.setStage(this.pageCursor);
		
		this.searchbar = new MoTextField(1, this.fontRendererObj, this.col(4) + 5, this.row(3), 1, 11, 1, 1, 58, 8, 60, 20, 125, 10, TEXTFIELD);
		this.searchbar.setVisible(true);
		this.searchbar.setEnabled(true);
		this.searchbar.setEnableBackgroundDrawing(true);
		this.searchbar.setTextColor(ENABLED_TEXT_COLOR);
		this.searchbar.setMaxStringLength(MAX_NAME_LEN);
		this.searchbar.setGuiResponder(new PlayerListQuery());
		
		this.okButton = new MoIconButton(-1, this.getGlobalX(195), this.row(2) - 5, 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, OKBUTTON);
		this.clearTooltip(this.okButton.id);
		this.addTooltip(this.okButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".ok") + TextFormatting.YELLOW + "(Enter)");
		
		this.cancelButton = new MoIconButton(-2, this.getGlobalX(220), this.row(2) - 5, 0, 90, 0, 0, 20, 20, 90, 90, 90, 180, CANCELBUTTON);
		this.clearTooltip(this.cancelButton.id);
		this.addTooltip(this.cancelButton.id, TextFormatting.AQUA + I18n.format(this.getUnlocalizedName() + ".no") + TextFormatting.YELLOW + "(Esc)");
		
		this.playerButtons.clear();
		for(int i = 0; i < MAX_PLAYER_PER_PAGE; i++)
		{
			int x = this.getGlobalX(19) + PLAYER_BUTTON_WIDTH  * (i % PLAYER_IN_ROW);
			int y = this.getGlobalY(43) + PLAYER_BUTTON_HEIGHT * (i / 3);
			
			GuiPlayerButton player = new GuiPlayerButton(i, x, y, PLAYER_BUTTON_WIDTH, PLAYER_BUTTON_HEIGHT);
			this.playerButtons.add(player);
		}
		
		this.markDirty();
	}
	
	public void markDirty()
	{
		this.isDirty = true;
	}
	
	@Override
	public void updateScreen() 
	{
		if(this.pageCursor != this.scrollbar.getStage()) //page changes
		{
			this.markDirty();
		}
		
		if(this.isDirty)	
		{
			this.pageCursor = this.scrollbar.getStage();
			
			for(int i = 0; i < MAX_PLAYER_PER_PAGE; i++)
			{
				int playerListIndex = MAX_PLAYER_PER_PAGE  * this.pageCursor + i;
				if(playerListIndex < this.filteredPlayerList.size())
				{
					this.playerButtons.get(i).setPlayer(this.filteredPlayerList.get(playerListIndex));
					
					if(this.filteredPlayerList.get(playerListIndex).equals(this.currentFocused))
					{
						this.playerButtons.get(i).setFocused(true);
					}
					else
					{
						this.playerButtons.get(i).setFocused(false);
					}
				}
				else
				{
					this.playerButtons.get(i).setPlayer(null);
					this.playerButtons.get(i).setFocused(false);
				}
			}
			
			this.isDirty = false;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) 
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		MoGuiScreen.drawProportionTexturedRect(TEXTURE, this.offsetX, this.offsetY, this.zLevel, 0, 0, 248, 166, 256, 256, this.guiWidth, this.guiHeight);
		this.scrollbar.drawScrollBar();
		
		//title
		this.drawCenteredString(this.fontRendererObj, this.textGuiTitle, this.getCenterX(), this.row(1), this.fontRendererObj.getColorCode('1'));
		
		//search bar
		this.fontRendererObj.drawString(this.textSearchbar, this.getGlobalX(19), this.row(3), this.fontRendererObj.getColorCode('0'));
		this.searchbar.drawTextBox();
		
		//ok
		this.okButton.drawButton(mc, mouseX, mouseY);
		
		//cancel
		this.cancelButton.drawButton(mc, mouseX, mouseY);
		
		//draw candidate receivers
		for(GuiPlayerButton player: this.playerButtons)
		{
			player.drawButton(mc, mouseX, mouseY);
		}
		
		if(this.okButton.isHovered(mouseX, mouseY))
		{
			this.drawTooltip(this.okButton.id, mouseX, mouseY);
		}
		else if(this.cancelButton.isHovered(mouseX, mouseY))
		{
			this.drawTooltip(this.cancelButton.id, mouseX, mouseY);
		}
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException 
	{
		if(keyCode == 200)	//key up
    	{
    		this.scrollbar.moveBackStage();
    		return;
    	}
    	else if(keyCode == 208)	//key down
    	{
    		this.scrollbar.moveNextStage();
    		return;
    	}
    	else if(keyCode == 28 || keyCode == 156 || keyCode == 1) //enter & esc
    	{
    		GameProfile selected = (keyCode == 1)? null: this.currentFocused;
    		this.responseAndDisplayParent(selected);
    	}
		
		if(this.searchbar.textboxKeyTyped(typedChar, keyCode))
		{
			return;
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException 
	{
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		if(this.scrollbar.isScrollBarClicked(mouseX, mouseY))
		{
			this.scrollbar.mouseClicked(mouseX, mouseY);
			return;
		}
		else if(this.scrollbar.isScrollFieldClicked(mouseX, mouseY))
		{
			this.scrollbar.scrollFieldClicked(mouseX, mouseY);
			return;
		}
		else if(this.okButton.mouseClick(mc, mouseX, mouseY, mouseButton))
		{
			this.responseAndDisplayParent(this.currentFocused);
			return;
		}
		else if(this.cancelButton.mouseClick(mc, mouseX, mouseY, mouseButton))
		{
			this.responseAndDisplayParent(null);
			return;
		}
		
		this.searchbar.mouseClicked(mouseX, mouseY, mouseButton);
		
		for(GuiPlayerButton player: this.playerButtons)
		{
			if(player.mouseClick(this.mc, mouseX, mouseY, mouseButton))
			{
				player.toggleFocused();
				this.currentFocused = (player.getFocused())? player.getPlayerProfile(): null;
				this.markDirty();
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
	
	@Override
	public void onGuiClosed() 
	{
		Keyboard.enableRepeatEvents(false);
	}
	
	private void responseAndDisplayParent(GameProfile selected)
	{
		this.parent.setReceiver(selected);
		this.changeGui(this.parent);
	}
	
	public void filterPlayerListByKeyword(List<String> keywords)
	{
		this.filteredPlayerList.clear();
		
		for(GameProfile profile: this.playerList)
		{
			String name = profile.getName();
			
			boolean isMet = true;
			for(String keyword: keywords)
			{
				if(!name.contains(keyword))
				{
					isMet = false;
					break;
				}
			}
			
			if(isMet)
			{
				this.filteredPlayerList.add(profile);
			}
		}
		
		this.markDirty();
	}
	
	private class PlayerListQuery implements GuiResponder
	{
		@Override
		public void setEntryValue(int id, boolean value) {}
		
		@Override
		public void setEntryValue(int id, float value) {}
		
		@Override
		public void setEntryValue(int id, String value) 
		{
			GuiSelectPlayer.this.filterPlayerListByKeyword(Arrays.asList(value.split(" ")));
		}
	}
}
