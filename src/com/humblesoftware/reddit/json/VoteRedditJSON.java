package com.humblesoftware.reddit.json;

import java.util.Hashtable;

import com.humblesoftware.reddit.HTTPRequest;
import com.humblesoftware.reddit.HTTPRequestThread;
import com.humblesoftware.reddit.RedditSession;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

public class VoteRedditJSON extends RedditJSON 
{
	/**
	 * dir = 1, 0, or -1. "direction" of vote.
	 */
	protected int voteDir = 0;
	
	/**
	 * subreddit name (actual String name)
	 */
	protected String subreddit = "";
	
	/**
	 * id of thing we're voting for   for instance: "name": "t3_9xl2k"
	 */
	protected String id = "";
	
	/**
	 * VoteRedditJSON constructor
	 * 
	 * @param voteDir
	 * @param subreddit
	 * @param id
	 */
	public VoteRedditJSON(int voteDir, String subreddit, String id)
	{
		this.voteDir = voteDir;
		this.subreddit = subreddit;
		this.id = id;
	}

	public void loadJSON()
	{
		Hashtable postData = new Hashtable();
		postData.put("id", this.id);
		postData.put("dir", Integer.toString(this.voteDir));
		postData.put("r", this.subreddit);
		postData.put("uh", RedditSession.getInstance().getModHash());
		
		HTTPRequestThread requestThread = new HTTPRequestThread(this.getVoteURL(), postData);
    	requestThread.start();
	}
	
	public String getVoteURL()
	{
		return "http://www.reddit.com/api/vote";
	}
	
	public void requestListener(final HTTPRequest request) 
	{
    	UiApplication.getUiApplication().invokeLater(new Runnable()
    	{
    		public void run()
    		{
    			
    			Dialog.alert("Done voting: " + request.getHTTPReponseText());
    			Dialog.alert(" " + request.getURL() + " " + request.getParams() + " " + RedditSession.getInstance().getCookie());
    		}
    	});
		//vote is submitted
	}
}
