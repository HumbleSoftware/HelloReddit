package com.humblesoftware.reddit;

import java.util.Hashtable;

/**
 * Threaded HTTPRequest
 */
public class HTTPRequestThread extends Thread 
{
	protected HTTPRequest request;
	
	public HTTPRequestThread(String url)
	{
		this.request = new HTTPRequest(url);
	}
	
    public HTTPRequestThread(String url, Hashtable postData)
    {
    	this.request = new HTTPRequest(url, postData);
    }
    
    public HTTPRequestThread(String url, Hashtable params, long method)
    {
    	this.request = new HTTPRequest(url, params, method);
    }
    
    public HTTPRequestThread(String url, Hashtable params, byte[] file, String fileField, String fileName, String fileType)
    {
    	this.request = new HTTPRequest(url, params, file, fileField, fileName, fileType);
    }
    
	public void run()
	{
		this.request.request();
		Event.trigger(this, "LOADED");
		
	}
	
	public HTTPRequest getHTTPRequest()
	{
		return this.request;
	}
	
	public String getHTTPReponseText()
	{
		return this.request.getHTTPReponseText();
	}
	
	public String getSetCookie()
	{
		return this.request.getSetCookie();
	}
}