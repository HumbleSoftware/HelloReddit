package com.humblesoftware.reddit.json;


import org.json.me.JSONArray;
import org.json.me.JSONObject;

import com.humblesoftware.reddit.Event;
import com.humblesoftware.reddit.HTTPRequestThread;
import com.humblesoftware.reddit.Listener;


/**
 * ArticleRedditJSON Class
 * 
 * This class retrieves the top articles from the Reddit's JSON API.  It then
 * loops through the list of articles, extracting the data of each.
 */
public class ArticleRedditJSON extends RedditJSON
{
    protected JSONArray subredditArticles;
    protected JSONObject article; 
    protected String subreddit;
    protected boolean requestSuccess = false;
    
    /**
     * Constructor
     * 
     * @todo  Null pointer exception when URL is bad
     * @param subreddit  The name of a subreddit
     */
    public ArticleRedditJSON(String subreddit)
    {
    	this.subreddit = subreddit;
    }
    
    /**
     * Dispatches a new HTTPRequestThread to get Subreddit JSON
     */
    public void loadJSON()
    {
    	HTTPRequestThread requestThread = new HTTPRequestThread(this.getSubredditURL());
    	Event.observe(requestThread, "LOADED", this.requestListener);
    	requestThread.start();
    }
    
    public boolean getRequestSuccess()
    {
    	return requestSuccess;
    }
    public String getSubreddit()
    {
    	return this.subreddit;
    }
    
    /**
     * Sets the current article of the subreddit.
     * 
     * @param index  The index of the article: sets to the index'th article.
     * @return
     */
    public boolean setArticle(int index)
    {
        if (index >= this.getNumArticles()) 
        {
            return false;
        }
        try 
        {
            this.article = this.subredditArticles.getJSONObject(index).getJSONObject("data");
        } 
        catch (Exception e) 
        {
            System.out.println("EXCEPTION");
            e.printStackTrace();            
        }
        
        return true;
    }
       
    /**
     * Get the number of up votes of the article.
     * 
     * @return
     */
    public int getUps()
    {
        int ups = 0;
        
        try
        {
            ups = this.article.getInt("ups");
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }
        
        return ups;
    }
    
    /**
     * Get the number of down votes of the article.
     * 
     * @return
     */
    public int getDowns()
    {
        int downs = 0;
        
        try 
        {
            downs = this.article.getInt("downs");
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }
        
        return downs;
    }
    
    /**
     * Get the rating of the article as a percentage.
     * 
     * This calculates the percentage of voters who liked the article.
     * 
     * @return
     */
    public int getRating()
    {
        int ups = this.getUps();
        int downs = this.getDowns();
        
        int rating = 0;
        
        rating = (int) java.lang.Math.floor(
                100*(double)ups/(double)(downs+ups));
        
        return rating;
    }
    
    /**
     * Get the score of the article.
     * 
     * @return
     */
    public int getScore()
    {
        int score = 0;
        
        try 
        {
            score = this.article.getInt("score");
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }
        
        return score;
    }
    
    /**
     * Get the title of the article.
     * 
     * @return
     */
    public String getTitle()
    {
        String title = "";
        
        try 
        {
            title = this.article.getString("title");
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }
        
        return title;
    }
    /**
     * Get the URL of the article as a string.
     * 
     * @return
     */
    public String getURL()
    {
        String url = "";
        
        try 
        {
            url = this.article.getString("url");
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }
        
        return url;
    }
    /**
     * Get the URL of the articles thumbnail, if any.
     * 
     * @return
     */
    public String getThumbnail()
    {
    	String thumbnail = "";
    	
    	try
    	{
    		thumbnail = this.article.getString("thumbnail");
    	}
    	catch (Exception e)
    	{
    		thumbnail = null;
    	}
    	
    	return thumbnail;
    }
    /**
     * Get the number of comments.
     * 
     * @return
     */
    public int getNumComments()
    {
        int numComments = 0;
        
        try 
        {
            numComments = this.article.getInt("num_comments");
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }
        
        return numComments;
    }
    /**
     * Get whether or not the user likes the article.
     * 
     * This is returned as 1 for likes, -1 for dislikes, and 0 for null.
     * 
     * @return
     */
    public int getLikes()
    {
    	String likes = null;
    	
        try 
        {
        	likes = this.article.getString("likes");
        	
        	if (likes == "true")
        	{
        		return 1;
        	}
        	else if (likes == "false")
        	{
        		return -1;
        	}
        	else
        	{
        		return 0;
        	}
        }
        catch (Exception e)
        {
            System.out.println("EXCEPTION");
            e.printStackTrace();
        }

        return 0;
    }
    /**
     * Get the total number of articles
     * 
     * @return
     */
    public int getNumArticles()
    {
        return this.subredditArticles.length();
    }
    
    /**
     * Get the url for this subreddit.
     * 
     * @return  A url for a subreddit.
     */
    public String getSubredditURL() 
    {
        return "http://www.reddit.com/r/"+this.subreddit+ "/.json";
    }
    /**
     * Get the ID of the article.
     * 
     * An article ID is the ID submitted in the permalink URL.
     * 
     * @return
     */
    public String getID()
    {
    	String id;
    	
    	try
    	{
    		id = this.article.getString("id");
    	}
    	catch (Exception e)
    	{
    		id = null;
    	}
    	
    	return id;
    }
    /**
     * Get the name of the article.
     * 
     * The article name is an identifying name used when making API calls.
     * 
     * @return
     */
    public String getName()
    {
    	String name;
    	
    	try
    	{
    		name = this.article.getString("name");
    	}
    	catch (Exception e)
    	{
    		name = null;
    	}
    	
    	return name;
    }
    /**
     * Get the domain of the article.
     * 
     * @return
     */
    public String getDomain()
    {
    	String domain;
    	
    	try
    	{
    		domain = this.article.getString("domain");
    	}
    	catch (Exception e)
    	{
    		domain = null;
    	}
    	
    	return domain;
    }
    /**
     * Get the permalink of the article.
     * 
     * @return
     */
    public String getPermalink()
    {
    	String permalink;
    	
    	try
    	{
    		permalink = this.article.getString("permalink");
    	}
    	catch (Exception e)
    	{
    		permalink = null;
    	}
    	
    	return permalink;
    }
    
    /**
     * Function to load JSON content from a JSON string.
     * 
     * @param jsonString
     */
	public void loadContent(String jsonString)
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
        
        if (myData != null)
        {
        	// Process the JSONObject extracting the articles.
            try
            {
                // The data node of the JSON object
                myData = myData.getJSONObject("data");
                
                // Get the array of articles
                this.subredditArticles = myData.getJSONArray("children");
                
                // If there are articles in the subreddit, get the first article.
                if (this.getNumArticles() > 0) 
                {
                    this.setArticle(0);
                } else {
                    this.article = null;
                }
                
                this.requestSuccess = true;
            }
            catch (Exception e)
            {
            	this.requestSuccess = false;
            }
        }
        
        Event.trigger(this, "LOADED");
	}
	/**
	 * Listener for HTTP request
	 */
    public Listener requestListener = new Listener()
    {
    	public void callback(Object r)
    	{
    		String response = ((HTTPRequestThread) r).getHTTPReponseText();
    		loadContent(response);
    	}
    };
}