package me.momocow.moemail.client.gui;

import me.momocow.mobasic.client.gui.MoGuiResponder;
import me.momocow.mobasic.client.gui.widget.MoTextField;

public class MoTextAreaResponder implements MoGuiResponder<String>
{
	private MoTextArea display;
	
	public MoTextAreaResponder(MoTextArea textarea) 
	{
		this.display = textarea;
	}
	
	/**
	 * A {@link MoTextField} specified by the first argument, id, has new input.
	 */
	@Override
	public void setEntryValue(int id, String text) 
	{
		
	}
}
