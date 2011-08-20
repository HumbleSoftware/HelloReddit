package com.humblesoftware.reddit;

import com.humblesoftware.reddit.screens.SubredditScreen;

import net.rim.device.api.ui.UiApplication;

/**
 * This class acts as an entry point to the Blackberry application RedditBB
 */
public class Reddit extends UiApplication 
{
	
    public static void main(String[] args)
    {
        Reddit instance = new Reddit();
        instance.enterEventDispatcher();
    }  
    public Reddit() 
    {
    	SubredditScreen s = new SubredditScreen("reddit.com");
    	//TestScreen s = new TestScreen();
    	//TestHelloRedditScreen s = new TestHelloRedditScreen();
    	pushScreen(s);
    }
}