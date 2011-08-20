package com.humblesoftware.reddit.screens;

import java.util.Stack;

import com.humblesoftware.blackberry.AnimatedGIFField;
import com.humblesoftware.reddit.Event;
import com.humblesoftware.reddit.Listener;
import com.humblesoftware.reddit.RedditSession;
import com.humblesoftware.reddit.json.ArticleRedditJSON;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.GIFEncodedImage;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;


/**
 * The login screen for HelloReddit
 * 
 * This class displays text boxes for the user's username and password.
 * The login button is tied to the LoginFieldChangeListener, 
 * which does the real login work.
 */
public class LoginScreen extends HelloRedditScreenBase
{

	/**
	 * buttonPressed prevents the login button from making multiple attempts at logging in
	 */
	protected boolean buttonPressed = false;
    
	/**
     * unameEditField is where the user inputs his/her username.
     * It is set as static so that other classes can retrieve it.
     */
    protected EditField unameEditField = new EditField();
    
    /**
     * pwEditField is where the user inputs his/her password.
     * It is set as static so that other classes can retrieve it.
     */
    protected PasswordEditField pwEditField = new PasswordEditField();
    
    /**
     * This constructor sets up the display.
     */
    public LoginScreen()
    {
        super();
        
        this.unameEditField.setLabel("Username: ");
        this.unameEditField.setMargin(24,0,0,8);
        
        this.pwEditField.setLabel("Password: ");
        this.pwEditField.setMargin(12,0,0,8);
        
        // Login button with listener
        ButtonField loginButton = new ButtonField(
        	"Login to Reddit", 
        	ButtonField.FIELD_HCENTER|ButtonField.CONSUME_CLICK
        );
        loginButton.setMargin(16,0,0,0);
        loginButton.setChangeListener( new FieldChangeListener()
    	{
    		/**
    	 	* Called the login button is pressed
    	 	*/
    		public void fieldChanged(Field field, int context)
    		{
    				makeSessionLogin();
    		}
    	});

        this.add(unameEditField);
        this.add(pwEditField);
        this.add(loginButton);
        
        Event.observe(RedditSession.getInstance(), "LOGIN", this.loginListener);
    }
    
    /**
     * Method that waits for the login button to be pressed, and then sets up the RedditSession
     */
    public void makeSessionLogin()
    {
    	if(!this.buttonPressed)
    	{
    		this.buttonPressed = true;
    		RedditSession s = RedditSession.getInstance();
    		s.login(getUsername(), getPassword(), this);
    	}
    }
 
    /**
     * override the "save/discard/cancel" prompt
     * @return always true
     */
    protected boolean onSavePrompt()
    {
        return true;
    } 
    
    /**
     * Return whatever the user typed into the username box
     * 
     * @return Whatever the user typed into the username box
     */
    public String getUsername()
    {
        return unameEditField.getText();
    }
    
    /**
     * Return whatever the user typed into the password box
     * 
     * @return Whatever the user typed into the password box
     */
    public String getPassword()
    {
        return pwEditField.getText();
    }
    
    /**
     * Sets the password field to blank, for security.
     */
    public void clearPassword()
    {
        pwEditField.setText("");
    }
    
    public Listener loginListener = new Listener()
	{
		public void callback(Object object)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
	    	{
	    		public void run()
	    		{
	    			successfulLogin();
	    		}
	    	});
		}
	};
    
	/**
	 * Method that waits for this observer to be called, letting you know that the login was successful
	 */
	public void successfulLogin()
	{
		//Do things, since the login was successful
		RedditSession s = RedditSession.getInstance();
		
		if (s.isLoggedIn())
		{
			UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
			Dialog.alert("Welcome " + s.getUsername() + "!");
			Event.stopObserving(s, "LOGIN", this.loginListener);
		}
		else
		{
			Dialog.alert("Login Unsuccessful");
			this.buttonPressed = false;
		}
	}
}
