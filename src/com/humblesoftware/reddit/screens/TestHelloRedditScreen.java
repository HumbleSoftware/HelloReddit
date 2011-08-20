package com.humblesoftware.reddit.screens;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class TestHelloRedditScreen extends HelloRedditScreenBase
{
	public TestHelloRedditScreen()
	{
		super();

		//RedditArticle ra = new RedditArticle(10, 5, 2, 100, 50, 0, "asdf", "Name", "<a href=\"http://www.google.com/\">Title</a>", "URL", "Domain", "");
		//this.add(ra);
		
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
		}
	}
}