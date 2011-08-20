package com.humblesoftware.reddit.screens;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
public class TestScreen extends HelloRedditScreenBase 
{
	/**
	 * OnClick Test
	 */
	public TestScreen ()
	{
		for(int i=0;i<=40;i++)
        {
			TestLabelField f = new TestLabelField(i+": This is an annoying test.");
			this.add(f);
        } 
	}
	public class TestLabelField extends VerticalFieldManager
	{
		protected String string;
		
		TestLabelField(String string)
		{
			super(Field.FOCUSABLE);
			
			this.string = string;
			
			LF f = new LF(string, Field.FOCUSABLE);
			this.add(f);
		}
		
		protected boolean navigationClick(int status, int time)
		{
			Dialog.alert(this.string);
			
			return true;
		}
		
		public class LF extends LabelField
		{
			LF (String string, long focusable) 
			{
				super(string, focusable);
			}
			protected void onFocus(int direction)
			{
				
			}
		}
	} 
	
	/**
	 * Focus Changing Test
	 */
/*	public TestScreen()
	{
		for(int i=0;i<=40;i++)
        {
			VerticalFieldManager v = new VerticalFieldManager(Field.FOCUSABLE);
			LabelField f = new LabelField(i+": This is an annoying test.", Field.FOCUSABLE);
			v.add(f);
			this.add(v);
        } 
	}
	
	
	public class RedditArticleField extends LabelField
	{
		RedditArticleField(String content, Font font)
		{
			super(content, Field.FOCUSABLE);
			
			
			this.setFont(font);
		}
		public void onFocus(int direction)
		{
			doFocus();
		}
		public void onUnfocus()
		{
			doFocus();
		}
		protected void drawFocus(Graphics graphics, boolean on) {}
	}
	
	public class RedditArticleField extends Field
	{
		protected String content;
		protected int fieldWidth;
		protected int fieldHeight;
		protected Font fieldFont;
		
		RedditArticleField(String content, Font font)
		{
			super(Manager.FOCUSABLE);

			fieldFont = font;
			fieldWidth = fieldFont.getAdvance(content)+2;
			fieldHeight = fieldFont.getHeight() + 3;
			
			this.content = content;
		}
		protected void drawFocus(Graphics graphics,
                boolean on)
		{}
		public void onFocus(int direction)
		{
			doFocus();
		}
		public void onUnfocus()
		{
			doFocus();
		}
		public int getPreferredWidth() {
			return fieldWidth;
		}

		public int getPreferredHeight() {
			return fieldHeight;
		}
		public boolean isSelectable() {
			return false;
		}
		
		protected void layout(int arg0, int arg1) {
			this.setExtent(getPreferredWidth(), getPreferredHeight());
		}
		
		protected void paint(Graphics graphics) {
			if (active) {
				graphics.setColor(maskColour);
				graphics.fillRect(0, 0, fieldWidth, fieldHeight);
			} else {
				graphics.setColor(backgroundColour);
				graphics.fillRect(0, 0, fieldWidth, fieldHeight);
			}

			graphics.setColor(textColour);
			graphics.setFont(fieldFont);
			graphics.drawText(content, 1, 1);
			graphics.drawLine(1, fieldHeight-2, fieldWidth-2, fieldHeight-2);
			graphics.setFont(this.fieldFont);
			graphics.drawText(this.content, 1, 1);
		}
	}
*/
}
