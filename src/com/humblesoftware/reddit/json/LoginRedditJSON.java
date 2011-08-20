package com.humblesoftware.reddit.json;

import java.util.Hashtable;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import com.humblesoftware.reddit.Event;
import com.humblesoftware.reddit.HTTPRequestThread;
import com.humblesoftware.reddit.Listener;

/**
 * Reddit Login JSON API Class
 * 
 * This class handles login API requests for reddit.com.  The API is located at
 * www.reddit.com/api/login/username.  It accepts three parameters:
 * api_type=json, user=username, and passwd=password.  For a successful login 
 * attempt the API will return a JSON object with a 'cookie' member and a 
 * 'modhash' member.  Both should be sent with future state changing requests to 
 * the reddit API.  The function of the 'modhash' is to prevent CSRF attacks.
 */
public class LoginRedditJSON extends RedditJSON 
{
	/**
	 * @member  The username of the user logging in.
	 */
	protected String username = null;
	/**
	 * @member  The password of the user logging in.
	 */
	protected String password = null;
	/**
	 * @member  On successful login, the user hash for preventing csrf.
	 */
	protected String modhash = null;
	/**
	 * @member  On successful login, the reddit session cookie value.
	 */
	protected String redditSession = null;
	/**
	 * @member  On successful login, the reddit 'Set-Cookie' response.
	 */
	protected String cookie = null;
	/**
	 * @member For successful login
	 */
	protected boolean loginSuccess = false;
	
		
	/**
	 * Constructor
	 * 
	 * @param username
	 * @param password
	 */
	public LoginRedditJSON(String username, String password)
	{
		this.username = username;
		this.password = password;
	}
	
    /**
     * Dispatches a new HTTPRequestThread to get Subreddit JSON
     */
    public void loadJSON()
    {
    	Hashtable postData = new Hashtable();
    	postData.put("user", this.username);
    	postData.put("passwd", this.password);
    	postData.put("api_type", "json");
    	
    	HTTPRequestThread requestThread = new HTTPRequestThread(this.getLoginURL(), postData);
    	Event.observe(requestThread, "LOADED", this.requestListener);
    	requestThread.start();
    }
    
	/**
	 * Session Modhash Getter
	 * 
	 * This is a user hash associated with the users current session, used to 
	 * prevent CSRF style attacks.
	 * 
	 * @return
	 */
	public String getModhash()
	{
		return this.modhash;
	}
	/**
	 * Get Reddit Session value
	 * @return
	 */
	public String getRedditSession()
	{
		return this.redditSession;
	}
	
	/**
	 * Session Cookie Getter
	 * 
	 * @return
	 */
	public String getCookie()
	{
		return this.cookie;
	}
	/**
	 * Gets whether or not the login was performed successfully.
	 * 
	 * @return 
	 */
	public boolean getLoginSuccess()
	{
		return this.loginSuccess;
	}
	
	/**
	 * Get the URL for the reddit login API
	 * 
	 * @return
	 */
    public String getLoginURL() 
    {
        return "http://www.reddit.com/api/login/"+ this.username;
    }
    
    public Listener requestListener = new Listener()
    {
    	public void callback(Object r)
    	{
    		cookie = ((HTTPRequestThread) r).getSetCookie();
    		loadContent(((HTTPRequestThread) r).getHTTPReponseText());
    	}
    };
    
    /**
     * Listener that gets the HTTPResponse and collects modhash and cookie from it
     */
    public void loadContent(final String jsonString)
	{
        JSONObject myData = null;
    	
        // Try to build a JSONObject from the text
        try 
        {
        	myData = new JSONObject(jsonString);
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }

        if (myData == null)
        {
        	return;
        }
        
        // Process the JSONObject
        try
        {
            // The data node of the JSON object
            myData = myData.getJSONObject("json");
            
            JSONArray myErrors = myData.getJSONArray("errors");
            
            myData = myData.getJSONObject("data");
            
            this.modhash = myData.getString("modhash");
            this.redditSession = myData.getString("cookie");
            this.loginSuccess = true;
        }
		catch (Exception e)
		{
			this.loginSuccess = false;
		}
		
		Event.trigger(this, "LOADED");
	}
}
