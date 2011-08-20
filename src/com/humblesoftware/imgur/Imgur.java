package com.humblesoftware.imgur;

import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import org.json.me.JSONObject;

import com.humblesoftware.reddit.Event;
import com.humblesoftware.reddit.HTTPRequest;


import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.ui.component.Dialog;

public class Imgur 
{
	/**
	 * url to upload images and return a json string
	 */
	protected String url = "http://imgur.com/api/upload.json";
	/**
	 * our API key for imgur
	 */
	protected String key = "8ad122d50cf51a71fa6ddc64b11edabc";
	/**
	 * path on filesystem to the image
	 */
	protected String imgPath = null;
	/**
	 * a base64 encoded string of the image
	 */
	protected String encodedStr = null;
	/**
	 * the Imgur URL to the image
	 */
	protected String imgurURL = null;
	
	/**
	 * @member  The URLEncodedPostData object
	 */
	protected URLEncodedPostData postData = null;
		
	/**
	 * Constructor for Imgur
	 * 
	 * @param imgPath Path on the filesystem to the image
	 */
	public Imgur(String imgPath)
	{
		this.imgPath = imgPath;
	}
	
    /**
     * Dispatches a new HTTPRequestThread to get Subreddit JSON
     */
    public void submitPhoto()
    {
    	Hashtable postData = new Hashtable();
    	postData.put("key", this.key);
		
    	try
		{
    		FileConnection conn = (FileConnection) 
    			Connector.open( "file://" + this.imgPath, Connector.READ );
			
			InputStream is = conn.openInputStream();
		    
			//Dialog.alert(conn.fileSize() + "");
			
			byte[] fileBytes  = new byte[(int) conn.fileSize()];
			
			is.read(fileBytes);
			
			is.close();
			
			
			//HTTPRequest request = 
		    //public HTTPRequest(String url, Hashtable params, byte[] file, String fileField, String fileName, String fileType)
			
			HTTPRequest request = new HTTPRequest(this.url, postData, fileBytes, "image", this.imgPath, "image/png");
			request.request();
			
			/*
			HTTPRequestMultipart request = new HTTPRequestMultipart(
				this.url,
				postData,
				"image",
				this.imgPath,
				"image/png",
				fileBytes);
			*/
			//byte[] reply = request.send();
			
			final byte[] reply = request.getHTTPReponseText().getBytes();
			this.setImgurURL(new String(reply));
			Dialog.alert("URL: " + getImgurURL());
			Event.trigger(this, "LOADED");
		}
		catch (Exception e)
		{
			//Dialog.alert("Problem!");
		}
    }
	
    /**
     * Retrieves the url that Imgur gives us
     * 
     * @return String url to the image on imgur
     */
    public String getImgurURL()
    {
    	return this.imgurURL;
    }
    
    /**
     * parses the returned json string for the imgur URL
     * 
     * @param req returned httprequest
     */
    private void setImgurURL(String jsonString)
    {
    	// Get JSON httpResponseText
    	//String jsonString = req.getHTTPReponseText();
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
        
        // Process the JSONObject
        try
        {
            // The data node of the JSON object
            myData = myData.getJSONObject("rsp");
            
            myData = myData.getJSONObject("image");
            
            this.imgurURL = myData.getString("imgur_page");
        }
		catch (Exception e)
		{
			//Dialog.alert("Fail.");
		}
    }
}
