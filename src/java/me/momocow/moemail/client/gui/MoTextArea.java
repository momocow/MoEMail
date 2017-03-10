package me.momocow.moemail.client.gui;

import java.util.TreeMap;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class  MoTextArea
{	
	protected int textColor = 5987163;
	protected int x;
	protected int y;
	protected int width;
	protected int lineHeight;
	protected int maxTextboxLen;
	protected int maxDisplayLineCount;
	protected FontRenderer fontRendererObj;
	
	protected TreeMap<Integer, String> content = new TreeMap<Integer, String>();
	protected boolean needUpdateDisplay = true;
	protected boolean isFocused = false;
	protected boolean visible = true;
	protected boolean isEnabled = true;
	protected Predicate<Integer> contentLineValidator;
	protected CursorManager cursorManager = new CursorManager();
	protected Integer displayStartLine = Integer.valueOf(0);
	protected UpdatableGuiParent<Integer> parent;
	
	/**
	 * construct a textarea without texture for its textfields
	 * @param x
	 * @param y
	 * @param width
	 * @param lineHeight
	 * @param maxLineCount
	 * @param fontRendererObj
	 */
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
		this.maxDisplayLineCount = maxLineCount;
		this.fontRendererObj = fontRendererObj;
		this.maxTextboxLen = this.width - 20;
		this.contentLineValidator =  Predicates.<Integer>in(this.content.keySet());
	}
	
	public void setParent(UpdatableGuiParent<Integer> parentIn)
	{
		this.parent = parentIn;
	}
	
	private int getLineY(int lineIndex)
	{
		return this.y + lineIndex * this.lineHeight;
	}
	
	public boolean keyTyped(char typedChar, int keyCode)
	{
		if(this.isFocused)
		{
			//no matter where the cursor is			
			if(keyCode == 201)	//page up
			{
				if(this.displayStartLine > 0)
				{
					this.parent.onUpdate(this.displayStartLine - 1);
				}
				return true;
			}
			else if(keyCode == 209)	//page down
			{
				if(this.displayStartLine + this.maxDisplayLineCount < this.content.size())
				{
					this.parent.onUpdate(this.displayStartLine + 1);
				}
				return true;
			}
			
			//make some operation around the cursor
			this.markDirtyDisplay();
			
			if(keyCode == 28 || keyCode == 156) //enter
			{
				this.insertChar('\n');
			}
			else if(keyCode == 200) //up
			{
				if(this.hasContentLine(this.getCursorLine() - 1))
				{
					int lasrLen = this.content.get(this.getCursorLine() - 1).replaceAll("\n", "").length();
					this.setCursorAt(this.getCursorLine() - 1, Math.min(this.getCursorPos(), lasrLen));
				}
			}
			else if(keyCode == 208) //down
			{
				if(this.hasContentLine(this.getCursorLine() + 1))
				{
					int nextLen = this.content.get(this.getCursorLine() + 1).replaceAll("\n", "").length();
					this.setCursorAt(this.getCursorLine() + 1, Math.min(this.getCursorPos(), nextLen));
				}
			}
			else if(keyCode == 205) //right
			{
				this.setCursorAt(this.getCursorLine(), this.getCursorPos() + 1);
			}
			else if(keyCode == 203) //left
			{
				this.setCursorAt(this.getCursorLine(), this.getCursorPos() - 1);
			}
			else if(keyCode == 14)	//backspace
			{
				if(this.getCursorLine() != 0 || this.getCursorPos() != 0)
				{
					if(this.getCursorPos() == 0) //begin of a paragraph, delete the line feed which is right at its previous position
					{
						int indexBeforeLF = this.content.get(this.getCursorLine() - 1).length() - 1;
						this.setCursorAt(this.getCursorLine() - 1, indexBeforeLF);
					}
					else
					{
						this.setCursorAt(this.getCursorLine(), this.getCursorPos() - 1);
					}
					
					this.deleteCharAfterCursor();
				}
			}
			else if(keyCode == 211)	//delete
			{
				this.deleteCharAfterCursor();
			}
			else if(keyCode == 207)	//end
			{
				if(GuiScreen.isCtrlKeyDown())	//set cursor to the end of the msg
				{
					this.cursorManager.setCursorAtContentEnd();
				}
				else	//set cursor to the end of this line
				{
					this.setCursorAt(this.getCursorLine(), this.content.get(this.getCursorLine()).replaceAll("\n", "").length());
				}
			}
			else if(keyCode == 199)	//home
			{
				if(GuiScreen.isCtrlKeyDown())	//set cursor to the begin of the msg
				{
					this.cursorManager.setCursorAtContentBegin();
				}
				else	//set cursor to the begin of this line
				{
					this.setCursorAt(this.getCursorLine(), 0);
				}
			}
			else if(ChatAllowedCharacters.isAllowedCharacter(typedChar)) //printable chars
			{
				this.insertChar(typedChar);
			}
			
			if(this.parent != null && !this.isCursorDisplay())
			{
				this.forceDisplayCursor();
			}
		}
		
		return false;
	}
	
	/**
	 * Manage the focus and cursor position
	 * @param mouseX
	 * @param mouseY
	 * @param mouseButton
	 */
	public void mouseClicked(int mouseX, int mouseY, int mouseButton)
	{	
		//none of the textfields is clicked
		//check if the textarea is clicked, if true, focus on the last enabled textfield
		if(this.isDominantArea(mouseX, mouseY))
		{
			this.isFocused = true;
			this.markDirtyDisplay();
			
			int clickedLine = this.displayStartLine + this.getClickedLine(mouseY);
			int clickedPos = this.getClickedPos(clickedLine, mouseX);
			if(this.hasContentLine(clickedLine) && clickedPos >= 0)
			{
				this.setCursorAt(clickedLine, clickedPos);
			}
			else
			{
				this.cursorManager.setCursorAtDisplayEnd();
			}
			return;
		}

		if(this.content.size() ==1 && this.content.get(0).isEmpty())
		{
			this.content.remove(0);
		}
		this.isFocused = false;
	}
	
	public void setDisplayStartLine(int lineIndex)
	{
		int maxStartLine = Math.max( 0, this.content.size() - this.maxDisplayLineCount);
		this.displayStartLine = MathHelper.clamp_int(lineIndex, 0, maxStartLine);
		this.markDirtyDisplay();
	}
	
	public void updateTextArea()
	{
		if(this.needUpdateDisplay && this.isFocused)
		{
			this.makeContent();
			this.cursorManager.updateCursor();
			
			this.displayUpdated();
		}
	}
	
	private void makeContent()
	{
		int lineId = 0;
		String lines = this.getContentString();
		this.resetContent();
		if(lines.isEmpty())
		{
			this.content.put(0, "");
		}
		else
		{
			for(; !lines.isEmpty(); lineId++)
			{
				String newContentLine = this.fontRendererObj.trimStringToWidth(lines, this.maxTextboxLen);
				
				int nl = -1;
				if((nl = newContentLine.indexOf("\n")) >= 0)
				{
					newContentLine = newContentLine.substring(0, nl + 1);
					this.content.put(lineId + 1, "");
				}
				
				this.content.put(lineId, newContentLine);
				lines = lines.substring(newContentLine.length());
			}
		}
	}
	
	public void drawTextArea()
	{
		if(this.visible)
		{
			for(int lineId = 0; this.displayStartLine + lineId < this.content.size() && lineId < this.maxDisplayLineCount; lineId++)
			{
				String line = new String(this.content.get(this.displayStartLine + lineId)).replaceAll("\n", "\u21b5");
				this.fontRendererObj.drawString(String.valueOf(this.displayStartLine + lineId + 1), this.x + 1, this.getLineY(lineId), this.fontRendererObj.getColorCode('7'));
				this.fontRendererObj.drawString(line, this.x + 9, this.getLineY(lineId), this.fontRendererObj.getColorCode('0'));
			}
			
			//draw cursor
			if(this.isFocused && this.isCursorDisplay())
			{
				int textWidth = this.content.get(this.getCursorLine()) != null? this.fontRendererObj.getStringWidth(this.content.get(this.cursorManager.cursor.getLine()).substring(0, this.cursorManager.cursor.getPos())): 0;
				int displayCursorX = this.x + 9 + textWidth;
				int displayCursorY = this.getLineY(this.getCursorLine() - this.displayStartLine);
				this.drawCursorVertical(displayCursorX, displayCursorY, displayCursorX + 1, displayCursorY + this.lineHeight - 1);
			}
		}
	}
	
	public int getHeight()
	{
		return this.lineHeight * this.maxDisplayLineCount;
	}
	
	public boolean isDominantArea(int posX, int posY)
	{
		return posX >= this.x && posY >= this.y && posX <= this.x + this.width && posY <= this.y + this.getHeight();
	}

	public void setTextColor(int colorInDec)
	{
		this.textColor = colorInDec;
	}
	
	public boolean hasContentLine(int line)
	{
		return this.contentLineValidator.apply(line);
	}
	
	public String getContentString()
	{
		return this.getContentString(0);
	}
	
	public String getContentString(int start)
	{
		String c = "";
		for(String line: Lists.newArrayList(this.content.values()).subList(start, this.content.size()))
		{
			c += line;
		}
		return c;
	}
	
	/**
	 * mark for the display screen to be updated, what should be marked dirty to force updated are listed as follows:
	 * <ul>
	 * <li>Text in any textfields changes</li>
	 * <li>Start line index changes (also refers to the first one)</li>
	 * </ul>
	 */
	public void markDirtyDisplay()
	{
		this.needUpdateDisplay = true;
	}
	
	protected void displayUpdated()
	{
		this.needUpdateDisplay = false;
	}
	
	public TreeMap<Integer, String> getContent()
	{
		return this.content;
	}
	
	/**
	 * Initialize a new map of content according to the provided one
	 * @param contentIn
	 */
	public void setContent(TreeMap<Integer, String> contentIn)
	{
		this.content = Maps.newTreeMap(contentIn);
		this.contentLineValidator = Predicates.<Integer>in(this.content.keySet());
	}
	
	public void resetContent()
	{
		this.content.clear();
	}
	
	/**
	 * delete a char right after the cursor, if there is nothing after the cursor(i.e. end of line), delete a char from the begin of next line if it exists
	 */
	public void deleteCharAfterCursor()
	{
		if(this.getCursorPos() < this.content.get(this.getCursorLine()).length()) //delete the char at the begin of next line
		{
			String newLine = this.content.get(this.getCursorLine()).substring(0, this.getCursorPos()) + this.content.get(this.getCursorLine()).substring(this.getCursorPos() + 1);
			this.content.put(this.getCursorLine(), newLine);
		}
		else if(this.content.get(this.getCursorLine() + 1).length() > 0)
		{
			String newLine = this.content.get(this.getCursorLine() + 1).substring(1);
			this.content.put(this.getCursorLine() + 1, newLine);
		}
	}
	
	public void insertChar(char ch)
	{
		String newLine = this.content.get(this.getCursorLine()).substring(0, this.getCursorPos()) 
				+ String.valueOf(ch) 
				+ this.content.get(this.getCursorLine()).substring(this.getCursorPos());
		this.content.put(this.getCursorLine(), newLine);
		this.makeContent();
		
		this.setCursorAt(this.getCursorLine(), this.getCursorPos() + 1);
	}
	
	public int getLineCount()
	{
		return this.content.size();
	}
	
	public void setFocused(boolean isFocusedIn)
	{
		this.isFocused = isFocusedIn;
	}
	
	public boolean isFocused()
	{
		return this.isFocused;
	}
	
	public void setVisible(boolean visibleIn)
	{
		this.visible = visibleIn;
	}
	
	public boolean isVisible()
	{
		return this.visible;
	}
	
	public void setEnabled(boolean isEnbledIn)
	{
		this.isEnabled = isEnbledIn;
	}
	
	public boolean isEnabled()
	{
		return this.isEnabled;
	}
	
	private int getClickedLine(int mouseY)
	{
		return mouseY / this.lineHeight;
	}
	
	private int getClickedPos(int line, int mouseX)
	{
		if(this.hasContentLine(line))
		{
			return this.fontRendererObj.trimStringToWidth(this.content.get(line), mouseX - this.x - 9).length();
		}
		
		return -1;
	}
	
	private int getCursorLine()
	{
		return this.cursorManager.cursor.getLine();
	}
	
	private int getCursorPos()
	{
		return this.cursorManager.cursor.getPos();
	}
	
	private void drawCursorVertical(int startX, int startY, int endX, int endY)
    {
        if (startX < endX)
        {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY)
        {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.x + this.maxTextboxLen)
        {
            endX = this.x + this.maxTextboxLen;
        }

        if (startX > this.x + this.maxTextboxLen)
        {
            startX = this.x + this.maxTextboxLen;
        }

        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos((double)startX, (double)endY, 0.0D).endVertex();
        vertexbuffer.pos((double)endX, (double)endY, 0.0D).endVertex();
        vertexbuffer.pos((double)endX, (double)startY, 0.0D).endVertex();
        vertexbuffer.pos((double)startX, (double)startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }
	
	public void setCursorAt(int lineIn, int posIn)
	{
		this.cursorManager.setCursorAt(lineIn, posIn);
	}
	
	public boolean isCursorDisplay()
	{
		return this.getCursorLine() >= this.displayStartLine && this.getCursorLine() < this.displayStartLine + this.maxDisplayLineCount;
	}
	
	public void forceDisplayCursor()
	{
		this.parent.onUpdate(this.getCursorLine());
	}
	
	private class CursorManager
	{
		MoTextArea textarea = MoTextArea.this;
		private CursorPos cursor = new CursorPos();
		
		public void setCursorAt(int lineId, int pos)
		{
			this.cursor.setCursor(lineId, pos);
		}
		
		/**
		 * Set the state of current cursor the same as the provided one. This is not done by direct assignment therefore the provided cursor is independent from the current one.
		 * @param cursorIn
		 */
		public void setCursorPos(CursorPos cursorIn)
		{
			this.setCursorAt(cursorIn.getLine(), cursorIn.getPos());
		}
		
		public void setCursorAtDisplayEnd()
		{
			for(int i = this.textarea.maxDisplayLineCount - 1; i >= 0; i--)
			{
				if(this.textarea.content.get(this.textarea.displayStartLine + i) != null)
				{
					this.setCursorAt(this.textarea.displayStartLine + i, this.textarea.content.get(this.textarea.displayStartLine + i).length());
					return;
				}
			}
		}
		
		public void setCursorAtContentEnd()
		{
			this.setCursorAt(this.textarea.content.size() - 1, this.textarea.content.lastEntry().getValue().length());
		}
		
		public void setCursorAtContentBegin()
		{
			this.setCursorAt(0, 0);
		}

		
		public CursorPos getCursorPos()
		{
			return this.cursor;
		}
		
		public void updateCursor()
		{
			this.cursor.computeCursorByIndex();
		}
		
		private class CursorPos
		{
			private MoTextArea textarea = CursorManager.this.textarea;

			private int line;
			private int pos;
			private int index;
			
			public void setCursor(int lineIndex, int posInLine)
			{
				if(this.textarea.hasContentLine(lineIndex) && posInLine <= this.textarea.content.get(lineIndex).length())
				{
					String prev = "";
					if(lineIndex > 0)
					{
						prev += this.textarea.getContentString(lineIndex - 1);
					}
					
					if(this.textarea.content.get(lineIndex) != null)
					{
						prev += this.textarea.content.get(lineIndex).substring(0, posInLine);
					}
					this.index = prev.length();
					
					this.computeCursorByIndex();					
				}
			}
			
			/**
			 * validate the line value and the position value according to the index field
			 */
			private void computeCursorByIndex()
			{
				int validatedLine = 0;
				int validatedIndex = this.index;

				while(true)
				{
					String currentLine = this.textarea.content.get(validatedLine);
					if(currentLine == null || validatedIndex <= currentLine.length())
					{
						if(currentLine != null && validatedIndex==currentLine.length() && !currentLine.isEmpty() && currentLine.charAt(currentLine.length() - 1) == '\n')
						{
							validatedLine++;
							validatedIndex = 0;
						}
						break;
					}
					
					validatedIndex -= currentLine.length();
					validatedLine++;
				}
				
				this.line = validatedLine;
				this.pos = validatedIndex;
			}
			
			public boolean isOutOfDisplayRange(int lineIndex)
			{
				return lineIndex < this.textarea.displayStartLine || lineIndex > this.textarea.displayStartLine + this.textarea.maxDisplayLineCount - 1;
			}
			
			public int getLine()
			{
				return this.line;
			}
			
			public int getPos()
			{
				return this.pos;
			}
		}
	}
	
	public interface UpdatableGuiParent<T>
	{
		public void onUpdate(T...info);
	}
	
	/**
	 * Copy text and cursor from another textarea, useful for {@link GuiScreen#initGui()} to laod a textarea from previously cached instance. 
	 * Changes will be made to the provided new instance only if the previous one is non-null, otherwise nothing changes. 
	 * The new instance will be returned no matter any changes occur or not.
	 * @return
	 */
	public static MoTextArea load(MoTextArea newOne, MoTextArea prevOne)
	{
		if(prevOne != null)
		{
			newOne.setContent(prevOne.getContent());
			newOne.cursorManager.setCursorPos(prevOne.cursorManager.getCursorPos());
			newOne.isFocused = prevOne.isFocused;
		}

		return newOne;
	}
}
