package com.humblesoftware.reddit;

import java.util.Hashtable;

import com.humblesoftware.reddit.json.LoginRedditJSON;
import com.humblesoftware.reddit.screens.LoginScreen;

import net.rim.device.api.system.CodeSigningKey;
import net.rim.device.api.system.ControlledAccess;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * Reddit Session Class
 * 
 * This manages the users session.
 */
public class RedditSession
{
	/**
	 * @member username 
	 */
	protected String username = null;
	/**
	 * @member modhash  User session modhash 
	 */
	protected String modhash = null;
	/**
	 * @member redditSession  Reddit session value
	 */
	protected String redditSession = null;
	/**
	 * @member cookie  Session cookie
	 */
	protected String cookie = null;
	/**
	 * @member loggedIn  Whether or not a logged in session is stored.
	 */
	protected boolean loggedIn = false;
	/**
	 * This is the key to session data within the persistent store.
	 * Text: Isn't this a Reddit key or is it maybe a Reddit key this isn't
	 * Key:  0x6b4f83fd654dbd6dL
	 * 
	 * @todo How to protect this information from other applications?
	 *       See: http://www.blackberry.com/developers/docs/4.5.0api/net/rim/device/api/system/ControlledAccess.html 
	 * @member storeKey
	 */
	protected long storeKey = 0x6b4f83fd654dbd6dL;
	/**
	 * @member singleRedditSession  Singleton instance
	 */
	protected static RedditSession singleRedditSession = null;
	
	/**
	 * Private constructor for singleton.
	 */
	private RedditSession()
	{
		// Load store if exists
		this.loadStore();
	}
	
	/**
	 * Singleton access function.
	 * 
	 * @return  Singleton instance.
	 */
	public static RedditSession getInstance() 
	{
		if (singleRedditSession == null) 
		{
			singleRedditSession = new RedditSession();
		}
		
		return singleRedditSession;
	}
	
	/**
	 * Logs in user.
	 * 
	 * @triggers "LOGIN"  On successful login.
	 * @param username
	 * @param password
	 * @param lscreen
	 * @return
	 */
	public boolean login(String username, String password, LoginScreen lscreen)
	{
		// Destroy existing session.
		//this.logout();
		
		// Begin new session.
		this.username = username;
		LoginRedditJSON login = new LoginRedditJSON(username, password);
		Event.observe(login, "LOADED", this.loginListener);
		login.loadJSON();
		
		return true;
	}
	
    /**
     * Destroys the session.
     * 
     * @return
     */
	public boolean logout()
	{
		Hashtable postData = new Hashtable();
		postData.put("top", "off");
		postData.put("uh", this.modhash);
		
		HTTPRequestThread requestThread = new HTTPRequestThread("http://www.reddit.com/logout", postData);
		Event.observe(requestThread, "LOADED", this.logoutListener);
		requestThread.start();
		
		return true;
	}
	/**
	 * Puts the session in the persistent store.
	 */
	protected void setStore()
	{
		PersistentObject store = PersistentStore.getPersistentObject( this.storeKey );
		
		//CodeSigningKey codeSigningKey = CodeSigningKey.get( CodeModuleManager.getModuleHandle( "HelloRedditSession" ), "HelloReddit" );
		CodeSigningKey codeSigningKey = CodeSigningKey.get("HelloReddit");
		
		synchronized(store) 
		{
			// Set stored values
			String[] session = new String[4];
			session[0] = this.username;
			session[1] = this.modhash;
			session[2] = this.cookie;
			session[3] = this.redditSession;
			
			store.setContents(new ControlledAccess(session, codeSigningKey));
			
			store.commit();
		}
	}
	/**
	 * Load session from persistent store.
	 */
	protected boolean loadStore()
	{
		PersistentObject store = PersistentStore.getPersistentObject( this.storeKey );
		
		synchronized(store) 
		{
			String[] session = (String[]) store.getContents();
			
			if (session == null) 
			{
				return false;
			} 
			else 
			{
				this.username = session[0];
				this.modhash = session[1];
				this.cookie = session[2];
				this.redditSession = session[3];
				
				//Check values
				if (   this.username != null 
					&& this.modhash  != null 
					&& this.cookie   != null )
				{
					this.loggedIn = true;
					return true;
				}
				else
				{
					return false;
				}
			}
		}
	}
//	//public class PersistentString extends String implements Persistable {}; 
//	public class SessionStore implements Persistable
//	{
//		public String username = null;
//		public String modhash = null;
//		public String cookie = null;
//		
//		SessionStore() {}
//	}
	/**
	 * Remove session from persistent store.
	 */
	protected void clearStore()
	{
		PersistentStore.destroyPersistentObject(this.storeKey);
	}
	/**
	 * Checks if there is a currently authenticated user session.
	 * 
	 * @return
	 */
	public boolean isLoggedIn()
	{
		return this.loggedIn;
	}
	/**
	 * Gets username.
	 * 
	 * @return
	 */
	public String getUsername()
	{
		return this.username;
	}
	/**
	 * Gets the cookie for the currently authenticated user.
	 * 
	 * @return
	 */
	public String getCookie()
	{
		return (this.cookie == null) ? "" : this.cookie;
	}
	/**
	 * Get the modhash for the currently authenticated user.
	 * 
	 * @return
	 */
	public String getModHash()
	{
		return this.modhash;
	}
	
	// Event Listeners
	/**
	 * Listener for login HTTP request.
	 */
	public Listener loginListener = new Listener()
    {
    	public void callback(Object r)
    	{
    		if (((LoginRedditJSON) r).getLoginSuccess())
    		{
    			redditSession = ((LoginRedditJSON) r).getRedditSession();
        		cookie = ((LoginRedditJSON) r).getCookie();
        		modhash = ((LoginRedditJSON) r).getModhash();
        		loggedIn = true;
        		
        		setStore();
    		}
    		
            Event.trigger(getInstance(), "LOGIN");
    	}
    };
    
    public Listener logoutListener = new Listener()
	{
    	public void callback(Object r)
    	{
    		// Destroy locally stored session
    		username = null;
    		cookie = null;
    		modhash = null;
    		loggedIn = false;
    		
    		// Destroy persistant session
    		clearStore();
    		
        	UiApplication.getUiApplication().invokeLater(new Runnable()
        	{
        		public void run()
        		{
        			Dialog.alert("Logged out!");
        		}
        	});
    		
    		Event.trigger(getInstance(), "LOGOUT");
    	}
	};
}