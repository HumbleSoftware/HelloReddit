package com.humblesoftware.reddit.ui;

import com.humblesoftware.reddit.Event;
import com.humblesoftware.reddit.HTTPBitmapField;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * Reddit Article Class
 */
public class RedditArticle extends HorizontalFieldManager
{
	protected String name = null;
	protected String url = null;
	protected String permalink = null;
	protected int likes = 0;
	protected BitmapField likesField = null;
	
	/**
	 * Constructor
	 * 
	 * @param ups
	 * @param downs
	 * @param rating
	 * @param numComments
	 * @param score
	 * @param title
	 * @param url
	 * @param thumbnail
	 */
	public RedditArticle(
		int ups, 
		int downs, 
		int rating, 
		int numComments, 
		int score, 
		int likes, 
		String id,
		String name,
		String title,
		String url,
		String domain,
		String permalink,
		String thumbnail)
	{
		super(HorizontalFieldManager.FOCUSABLE|HorizontalFieldManager.USE_ALL_WIDTH|HorizontalFieldManager.VERTICAL_SCROLL);
		
		this.name = name;
		this.url = url;
		this.permalink = permalink;
		this.likes = likes;
		this.likesField = new BitmapField();
		
        // Build scoringField to display points and rating
        String scoreString = score + " points - " + rating + "% like it - " + numComments + " comments (" + domain + ")";
        RedditArticleField scoreField = new RedditArticleField(scoreString,Font.getDefault().derive(0, 12));
        scoreField.setPadding(4, 4, 4, 4);
        
        // Build linkField to display title, num comments and url
        String linkString = title;
        RedditArticleField linkField = new RedditArticleField(linkString,Font.getDefault().derive(0, 14));
        linkField.setPadding(4, 4, 4, 4);
        
        if (thumbnail.length() > 0)
        {
        	final HTTPBitmapField thumbnailField = new HTTPBitmapField(thumbnail)
        	{
        		public void onFocus(int direction)
        		{
        			doFocus();
        		}
        		public void onUnfocus()
        		{
        			doFocus();
        		}
        	};
        	thumbnailField.setPadding(6, 4, 6, 6);
        	this.add(thumbnailField);
        }
        
        
        HorizontalFieldManager bottomManager = new HorizontalFieldManager(HorizontalFieldManager.FOCUSABLE|HorizontalFieldManager.VERTICAL_SCROLL);
    	bottomManager.add(this.likesField);
        if (this.likes == 1)
        {
        	this.voteUp();
        }
        else if (this.likes == -1)
        {
        	this.voteDown();
        }
        bottomManager.add(scoreField);
        
		VerticalFieldManager contentManager = new VerticalFieldManager(Field.FOCUSABLE|Field.USE_ALL_WIDTH|VerticalFieldManager.VERTICAL_SCROLL);
        contentManager.setPadding(2, 2, 2, 2);
        contentManager.add(new RedditArticleNullField());
        contentManager.add(linkField);
        contentManager.add(bottomManager);
        contentManager.add(new RedditArticleNullField());
        
        this.add(contentManager);
	}
	
	public int getLikes()
	{
		return this.likes;
	}
	
	public void voteUp()
	{
		this.likes = 1;
		
    	Bitmap bitmap = Bitmap.getBitmapResource("res/up.gif");
    	this.likesField.setPadding(4,0,0,8);
    	this.likesField.setBitmap(bitmap);
    	this.invalidate();
	}
	
	public void voteDown()
	{
		this.likes = -1;
		
		Bitmap bitmap = Bitmap.getBitmapResource("res/down.gif");
    	this.likesField.setPadding(4,0,0,8);
    	this.likesField.setBitmap(bitmap);
    	this.invalidate();
	}
	
	public void paint(Graphics graphics)
	{
		// Focused Paint Job: border and such
		if (this.isFocus())
		{
			graphics.clear();
			graphics.setColor(0x001B1BA2);
			graphics.drawRoundRect(1, 0, this.getWidth()-2, this.getHeight(), 20, 20);
			graphics.setColor(Color.BLACK);
			super.paint(graphics);
		}
		// Default Paint Job
		else
		{
			super.paint(graphics);
		}
	}
	/**
	 * Called by child elements to focus the main manager for the article.
	 */
	public void onFocus(int direction)
	{
		super.onFocus(direction);
		this.doFocus();
	}
	
	public void doFocus()
	{
		if (isFocus())
		{
			this.invalidate();
			Event.trigger(this, "FOCUS");
		}
	}
	
	public boolean isFocus()
	{
		if (super.isFocus()) 
		{
			return true;
		} 
		else if (this.getFieldWithFocus() != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getPermalink()
	{
		return this.permalink;
	}
	
	/**
	 * Click handler.
	 */
    protected boolean navigationClick(int status, int time)
	{
		synchronized (UiApplication.getEventLock()) {
			//Browser.getDefaultSession().displayPage(this.url+".mobile");
			Browser.getDefaultSession().displayPage(this.url);
		}
		return true;
	}
	
	/**
	 * Custom NullField
	 * 
	 * Passes up focus.  This allows trackwheel BlackBerries to grab focus of 
	 * articles.
	 * 
	 * @author Carl Sutherland
	 */
	public class RedditArticleNullField extends NullField
	{
		RedditArticleNullField()
		{
			super(FOCUSABLE);
		}
		
		public void onFocus(int direction)
		{
			super.onFocus(direction);
			doFocus();
		}
		public void onUnfocus()
		{
			doFocus();
		}
	}
	
	/**
	 * Custom LabelField Class
	 * 
	 * Allows for passing of focus.
	 * 
	 * @author Carl Sutherland
	 */
	public class RedditArticleField extends LabelField
	{
		RedditArticleField(String content, Font font)
		{
			super(content, Field.FOCUSABLE);
			
			this.setFont(font);
		}
		
		public boolean isSelectionCopyable()
		{
			return false;
		}
		
		public void onFocus(int direction)
		{
			super.onFocus(direction);
			doFocus();
		}
		
		public void onUnfocus()
		{
			doFocus();
		}
		
		protected void drawFocus(Graphics graphics, boolean on) {}
	}
}