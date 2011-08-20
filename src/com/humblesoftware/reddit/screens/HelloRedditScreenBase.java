package com.humblesoftware.reddit.screens;

import com.humblesoftware.blackberry.AnimatedGIFField;
import com.humblesoftware.reddit.RedditSession;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class HelloRedditScreenBase extends MainScreen
{
	/**
	 * @member Subordinate title field manager
	 */
	protected HorizontalFieldManager titleFieldManager = 
		new HorizontalFieldManager(Field.USE_ALL_WIDTH|Manager.FOCUSABLE)
		{
			public void paint(Graphics graphics)
			{
				graphics.setColor(Color.WHITE);
				Bitmap bitmap = Bitmap.getBitmapResource("res/title.gif");
				graphics.drawBitmap(0, 0, this.getWidth(), this.getHeight(), bitmap, 0, 0);
				super.paint(graphics);
			}
			protected void sublayout( int maxWidth, int maxHeight )
            {
                super.sublayout(maxWidth, maxHeight);
                setExtent(maxWidth, 24);
            }
		};
	
	/**
	 * @member Internal body field manager added to the mainManager. 
	 */
	protected VerticalFieldManager bodyFieldManager = 
		new VerticalFieldManager(
			Field.USE_ALL_WIDTH|Field.USE_ALL_HEIGHT|Manager.FOCUSABLE|
			Manager.VERTICAL_SCROLL|Manager.VERTICAL_SCROLLBAR);
	
	/**
	 * @member Wrapper manager of body field manager for styling (fixed bg).
	 */
	protected VerticalFieldManager decorManager = 
		new VerticalFieldManager(
			Field.USE_ALL_WIDTH|Field.USE_ALL_HEIGHT|Manager.FOCUSABLE|
			Manager.NO_VERTICAL_SCROLL|Manager.NO_VERTICAL_SCROLLBAR)
		{
			public void paint(Graphics graphics) 
			{
				Bitmap bitmap = Bitmap.getBitmapResource("res/shadow.gif");
				graphics.drawBitmap(0, 0, bitmap.getWidth(), bitmap.getHeight(), bitmap, 0, 0);
				super.paint(graphics);
			}
		};
		
		
	/**
	 * HelloRedditScreenBase Constructor
	 * 
	 * This constructor sets up the field managers, creates a menu, sets the
	 * title, and creates the styles for the screen.
	 */
	public HelloRedditScreenBase()
	{
		super(Manager.NO_VERTICAL_SCROLL|Screen.DEFAULT_CLOSE|Field.FOCUSABLE);
		
		// Setup the two managers
		super.add(this.titleFieldManager);
		this.decorManager.add(this.bodyFieldManager);
        super.add(this.decorManager);
        
		// Setup Title
		RichTextField applicationTitle = new RichTextField(
			"HelloReddit", RichTextField.TEXT_ALIGN_RIGHT|Field.NON_FOCUSABLE);
		applicationTitle.setPadding(4, 24, 0, 0);
		this.titleFieldManager.add(applicationTitle);
	}
	/**
	 * Add a field to the main vertical field manager.
	 * 
	 * @param field  Field to be added
	 */
	public void add(Field field)
	{
		this.bodyFieldManager.add(field);
	}
	/**
	 * Delete a field from the main vertical field manager.
	 * 
	 * @param field  Field to be deleted
	 */
	public void delete(Field field)
	{
		this.bodyFieldManager.delete(field);
	}
	/**
	 * Delete all fields from the main vertical field manager.
	 */
	public void deleteAll()
	{
		this.bodyFieldManager.deleteAll();
	}
	/**
	 * Get the center, in PX, of the body field manager.
	 * 
	 * @return
	 */
	public int getCenter()
	{
		return (Display.getHeight() - this.titleFieldManager.getPreferredHeight()) / 2;
	}
	/**
	 * Gets an animated loading gif field which can be added to a field manager 
	 * to give the user the visual cue of loading.
	 * 
	 * @return  The animated gif field
	 */
	protected AnimatedGIFField getLoadingGIF()
	{
	    EncodedImage f = EncodedImage.getEncodedImageResource("res/loading.gif");
	    AnimatedGIFField loadingField = new AnimatedGIFField((GIFEncodedImage) f, Field.FIELD_HCENTER);
	    
	    return loadingField;
	}
	
	protected boolean keyDown( int keycode, int status ) 
	{ 
		if ( Keypad.key( keycode ) == Keypad.KEY_ESCAPE ) 
		{ 
			System.exit( 0 ); 
			return true;
		} 
		return super.keyDown( keycode, status ); 
	}
	
    /**
     * Overloaded makeMenu function.
     * 
     * This selects menu items based upon the state of the session.
     * 
     * @param menu  Menu to which items are added.
     * @param instance  The instance of the desired menu.
     */
    public void makeMenu(Menu menu, int instance)
    {
    	//this.removeAllMenuItems();
    	
    	// Set up menu items:
        this.addMenuItem(this.subSelector);
    	this.addMenuItem(new RedditMenuItem("reddit.com", 0x00020001, 0));
        this.addMenuItem(new RedditMenuItem("pics", 0x00020002, 0));
        this.addMenuItem(new RedditMenuItem("programming", 0x00020003, 0));
        this.addMenuItem(new RedditMenuItem("politics", 0x00020004, 0));
        this.addMenuItem(new RedditMenuItem("funny", 0x00020005, 0));
        this.addMenuItem(this.aboutMenuItem);
        
    	if (RedditSession.getInstance().isLoggedIn()) 
    	{
    		// Menu Items for logged in users
    		this.addMenuItem(this.logoutMenuItem);
    		this.addMenuItem(this.imgurMenuItem);
    	}
    	else
    	{
    		// Menu Items for logged out users
    		this.addMenuItem(this.loginMenuItem);
    	}
    	
    	super.makeMenu(menu, instance);
    }
    /**
     * Clear the default menu when dismissed.
     */
    protected void onMenuDismissed(Menu menu)
    {
    	this.removeAllMenuItems();
    }
	/**
     * RedditMenuItem Class
     * 
     * A MenuItem class for menu items linking to new subreddit pages.
     * 
     * @todo Build a better check for active subreddits.
     * @author trevor tblanarik@gmail.com
     */
    class RedditMenuItem extends MenuItem
    {
        String mySubreddit;

        public RedditMenuItem(String text, int ordinal, int priority)
        {
            super("/r/"+text, ordinal, priority);
            this.mySubreddit = text;
        }

        public void run()
        {
        	//if (this.mySubreddit != null && !this.mySubreddit.equals(subreddit))
        	//{
        		SubredditScreen rs = new SubredditScreen(this.mySubreddit);
        		UiApplication.getUiApplication().pushScreen(rs);
        	//}
        }
    }
    /**
	 * Reddit imgur Menu Item
	 * 
	 * A menu item for submitting a imgur item
	 */
	MenuItem imgurMenuItem = new MenuItem("submit pic", 0x000A0000, 0)
    {
        public void run()
        {
        	SubmitScreen ss = new SubmitScreen();
        	
            //Push the submit screen on
            UiApplication.getUiApplication().pushScreen(ss);
            ss.setupSubmit();
        }
        
    };
    
	MenuItem subSelector = new MenuItem("go to subreddit", 0x00020000, 0)
    {
        public void run()
        {
        	 String choices[] = {"Ok","Cancel"}; 
        	 int values[] = {Dialog.OK,Dialog.CANCEL}; 
        	 final SubDialog diag = new SubDialog(choices,values); 
        	 UiApplication.getUiApplication().invokeLater(new Runnable() 
        	 { 
        		 public void run() 
        		 { 
        			 int iResponse = diag.doModal(); 
        			 if(iResponse == 0) 
        			 {	 
        	        	 SubredditScreen rs = new SubredditScreen(diag.getSubreddit().trim());
        	        	 UiApplication.getUiApplication().pushScreen(rs);
        			 } 
        	 } }); 
        }
        
    };
    
    
	/**
	 * @member loginMenuItem A menu item to login a user.
	 */
	protected MenuItem loginMenuItem = new MenuItem("login", 0x000A0001, 0) 
	{
		public void run()
		{
			UiApplication.getUiApplication().pushScreen(new LoginScreen());
		}
	};
	/**
	 * @member logoutMenuItem A menu item to login a user.
	 */
	protected MenuItem logoutMenuItem = new MenuItem("logout", 0x000A0001, 0)
	{
		public void run()
		{
			/**
			 * @todo Code the logout functionality
			 */
			RedditSession.getInstance().logout();
		}
	};
	
	/**
	 * @member aboutMenuItem A menu item to display a login screen.
	 */
	protected MenuItem aboutMenuItem = new MenuItem("about", 0x000A0002, 0) 
	{
		public void run()
		{
			UiApplication.getUiApplication().pushScreen(new AboutScreen());
		}
	};
}


