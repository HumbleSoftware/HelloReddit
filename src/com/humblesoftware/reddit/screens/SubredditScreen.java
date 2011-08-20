package com.humblesoftware.reddit.screens;

import com.humblesoftware.blackberry.AnimatedGIFField;
import com.humblesoftware.reddit.Event;
import com.humblesoftware.reddit.Listener;
import com.humblesoftware.reddit.RedditSession;
import com.humblesoftware.reddit.json.ArticleRedditJSON;
import com.humblesoftware.reddit.json.VoteRedditJSON;
import com.humblesoftware.reddit.ui.RedditArticle;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;


import net.rim.device.api.ui.component.*;

/**
 * RedditScreen Class for displaying a list of subreddit articles.
 * 
 * This class displays a screen containing articles from the requested
 * subreddit.
 */
public class SubredditScreen extends HelloRedditScreenBase
{
    /**
     * @member subreddit  Name of the subreddit this screen is displaying.
     */
    protected String subreddit = null;
    /**
     * @member RedditArticle  Currently selected article, if any.
     */
    protected RedditArticle article = null;
    /**
     * @member loadingGif  An animated gif displayed when loading.
     */
    protected AnimatedGIFField loadingGIF = null;
    
    /**
     * RedditScreen constructor
     * 
     * @param subreddit The (lowercase) name of the subreddit.
     */
    public SubredditScreen(String subreddit)
    {
        super();
        
        // Set members
        this.subreddit = subreddit;
        
        this.loadSubreddit();
        
        Event.observe(RedditSession.getInstance(), "LOGIN", this.loginStateListener);
        Event.observe(RedditSession.getInstance(), "LOGOUT", this.loginStateListener);
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
    	// If the user is logged in
    	if (RedditSession.getInstance().isLoggedIn()) 
    	{
    		// If an article has focus
    		if (this.article != null && this.article.getName() != "")
    		{
    			// Menu button to vote an article up
	            if (this.article.getPermalink() != null) 
	            {
		            this.addMenuItem( new MenuItem("comments", 0x00010000, 0)
		            {
		        		public void run()
		        		{
	        				synchronized (UiApplication.getEventLock())
	        				{
	        					Browser.getDefaultSession().displayPage("http://www.reddit.com"+article.getPermalink());
	        				}
		        		}
		        	});
	            }
	            
    			// Menu button to vote an article up
	            if (this.article.getLikes() != 1) 
	            {
		            this.addMenuItem( new MenuItem("vote up", 0x00010001, 0)
		            {
		        		public void run()
		        		{
		        			VoteRedditJSON myVote = new VoteRedditJSON(1,subreddit,article.getName());
		        			myVote.loadJSON();
		        			article.voteUp();
		        		}
		        	});
	            }
	            
	            if (this.article.getLikes() != -1)
	            {
		            // Menu button to vote an article down
		            this.addMenuItem( new MenuItem("vote down", 0x00010002, 0) 
		            {
		        		public void run()
		        		{
		        			VoteRedditJSON myVote = new VoteRedditJSON(-1,subreddit,article.getName());
		        			myVote.loadJSON();
		        			article.voteDown();
		        		}
		        	});
	            }
    		}
    	}
    	
    	super.makeMenu(menu, instance);
    }
    
    protected void loadSubreddit()
    {
    	// Loading Animation
        this.loadingGIF = this.getLoadingGIF();
        
    	// Get subreddit object following a reductive observer pattern.
        ArticleRedditJSON mySubreddit = new ArticleRedditJSON(this.subreddit);
        Event.observe(mySubreddit, "LOADED", this.requestListener);
        mySubreddit.loadJSON();

        // Display subreddit name
        RichTextField subredditField = new RichTextField(
    		"/r/"+this.subreddit,
    		RichTextField.NON_FOCUSABLE
		);
        SeparatorField separatorField = new SeparatorField();
        subredditField.setPadding(4, 0, 0, 4);
        separatorField.setPadding(0, 0, 4, 0);
        
        this.add(subredditField);
        this.add(separatorField);
        this.add(this.loadingGIF);
    }
    
    /**
     * Updates content with a new ArticleRedditJSON
     * 
     * @param mySubreddit  
     */
    protected void updateContent(final ArticleRedditJSON mySubreddit)
    {
    	// Remove the animated loading GIF.
    	if (!mySubreddit.getRequestSuccess())
    	{
    		UiApplication.getUiApplication().invokeLater(new Runnable() {
    			public void run()
    			{
    				Dialog.alert("Subreddit '"+mySubreddit.getSubreddit()+"' could not be loaded.");
    			}
    		});
    		return;
    	}
    	
    	this.delete(this.loadingGIF);
    	
		for (int i = 0; i < mySubreddit.getNumArticles(); ++i)
	    {
			// Set the current article
	        mySubreddit.setArticle(i);
	        
	        // Build the RedditArticle UI object.
	        RedditArticle article = new RedditArticle(
        		mySubreddit.getUps(), 
        		mySubreddit.getDowns(), 
        		mySubreddit.getRating(), 
        		mySubreddit.getNumComments(), 
        		mySubreddit.getScore(), 
        		mySubreddit.getLikes(),
        		mySubreddit.getID(), 
        		mySubreddit.getName(), 
        		mySubreddit.getTitle(), 
        		mySubreddit.getURL(), 
        		mySubreddit.getDomain(),
        		mySubreddit.getPermalink(),
        		mySubreddit.getThumbnail()
    		); 
	        
	        // Set up listeners
	        Event.observe(article, "FOCUS", this.focusListener);
	        
	        // Add the UI elements to the page
	        this.add(article);
	        SeparatorField f = new SeparatorField();
	        f.setPadding(4,4,4,4);
	        this.add(f);
	    }
		
		/**
		 *  @todo document this.
		 */
		Event.trigger(this, "CONTENT");
    }
    
    // Event Listeners
    
    /**
     * Listener for a JSON request.
     * 
     * Is called by ArticleRedditJSON to dispatch updateContent in the main 
     * event thread.
     * 
     * @param mySubreddit 
     */
    public Listener requestListener = new Listener()
	{
		public void callback(final Object mySubreddit)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
	    	{
	    		public void run()
	    		{
	    			updateContent((ArticleRedditJSON) mySubreddit);
	    		}
	    	});
		}
	};
	/**
	 * Listener for changes in article focus.
	 * 
	 * When the article in focus changes, this is called to update the state of
	 * the SubredditScreen for voting. 
	 */
	public Listener focusListener = new Listener()
	{
		public void callback(Object selectedArticle)
		{
			article = (RedditArticle) selectedArticle;
			invalidate();
		}
	};
	
	public Listener loginStateListener = new Listener()
	{
		public void callback(Object RedditSession)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
	    	{
	    		public void run()
	    		{
	    			deleteAll();
	    			loadSubreddit();
	    		}
	    	});
		}
	};
}
