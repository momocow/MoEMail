package me.momocow.moemail.client.gui;

import java.util.ArrayList;
import java.util.List;

import me.momocow.mobasic.client.gui.widget.MoTextField;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class MoTextArea 
{
	private static final int MAX_LINE_LEN = 80;
	
	protected int textColor = 5987163;
	protected int x;
	protected int y;
	protected int width;
	protected int lineHeight;
	protected ResourceLocation texture;
	protected int imageWidth;
	protected int imageHeight;
	protected int disabledX;
	protected int disabledY;
	protected int enabledX;
	protected int enabledY;
	protected int textureWidth;
	protected int textureHeight;
	protected int currentLineStart;
	protected int maxTextboxLen;
	protected int maxLineCount;
	protected FontRenderer fontRendererObj;
	private boolean isFocused = false;
	private List<MoTextField> textfields = new ArrayList<MoTextField>();
	private String content = "";
	private int cursor = 0;
	
	public MoTextArea(int x, int y, int width, int lineHeight, int maxLineCount, FontRenderer fontRendererObj) 
	{
		this(x, y, width, lineHeight, maxLineCount, fontRendererObj, 0, 0, 0, 0, 0, 0, 0, 0, null);
	}
	
	public MoTextArea(int x, int y, int width, int lineHeight, int maxLineCount, FontRenderer fontRendererObj,
    		int disable_x, int disable_y, 
    		int enable_x, int enable_y, 
    		int txtWidth, int txtHeight, 
    		int imgWidth, int imgHeight, 
    		ResourceLocation txt) 
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.lineHeight = lineHeight;
		this.maxLineCount = maxLineCount;
		this.fontRendererObj = fontRendererObj;
		this.imageWidth = imgWidth;
		this.imageHeight = imgHeight;
		this.disabledX = disable_x;
		this.disabledY = disable_y;
		this.enabledX = enable_x;
		this.enabledY = enable_y;
		this.textureWidth = txtWidth;
		this.textureHeight = txtHeight;
		this.texture = txt;
		this.maxTextboxLen = this.width - 20;
		
		//init each line with a textfield
		for(int i = 0; i < this.maxLineCount; i++)
		{
			MoTextField tf = new MoTextField(i, this.fontRendererObj, this.x + 5, this.getLineY(0 + i), 
					this.disabledX, this.disabledY, this.enabledX, this.enabledY, this.textureWidth, this.textureHeight, this.imageWidth, this.imageHeight, 
					this.width - 10, this.lineHeight, this.texture);
			//always enable the first line
			if(i != 0)
			{
				tf.setVisible(false);
				tf.setEnabled(false);
			}
			tf.setTextColor(this.textColor);
			tf.setMaxStringLength(MAX_LINE_LEN);
			this.textfields.add(tf);
		}

	}
	
	private int getLineY(int lineIndex)
	{
		return this.y + lineIndex * this.lineHeight;
	}
	
	public boolean keyTyped(char typedChar, int keyCode)
	{
	}
	
	public void mouseClicked(int mouseX, int mouseY, int mouseButton)
	{
	
	}
	
	/**
	 * Responsible for updating information needed to draw the screen
	 * @param startLine
	 */
	public void updateTextArea()
	{
		
	}
	
	public void drawTextArea()
	{
		for(int i = 0; i < this.maxLineCount; i++)
		{
			this.textfields.get(i).drawTextBox();
		}
	}
	
	public int getHeight()
	{
		return this.lineHeight * this.maxLineCount;
	}
	
	public boolean isDominantArea(int posX, int posY)
	{
		return posX >= this.x && posY >= this.y && posX <= this.x + this.width && posY <= this.y + this.getHeight();
	}

	public void setTextColor(int colorInDec)
	{
		this.textColor = colorInDec;
	}
}
