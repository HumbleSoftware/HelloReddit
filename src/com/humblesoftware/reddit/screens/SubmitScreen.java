package com.humblesoftware.reddit.screens;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;


import java.util.Hashtable;

import org.json.me.JSONArray;
import org.json.me.JSONObject;

import com.humblesoftware.imgur.Imgur;
import com.humblesoftware.reddit.Event;
import com.humblesoftware.reddit.HTTPBitmapField;
import com.humblesoftware.reddit.HTTPRequestThread;
import com.humblesoftware.reddit.Listener;
import com.humblesoftware.reddit.RedditSession;

public class SubmitScreen extends HelloRedditScreenBase 
{
	/**
	 * @member captchaID The ID used for the captcha URL/iden
	 */
	protected static String captchaID;
	/**
	 * @member title The title for this reddit submission
	 */
	protected String title;
	/**
	 * @member url The url we're submitting. Not to be confused with the returned URL!
	 */
	protected String url;
	/**
	 * @member subreddit The subreddit we're submitting to
	 */
	protected String subreddit;
	
	/**
	 * @member titleField A field to enter the title of the article
	 */
	protected EditField titleField = new EditField();
	/**
	 * @member captchaField A field to ender the captcha, if needed
	 */
	protected EditField captchaField = new EditField();
	
	/**
	 * @member urlField A field to display the imgur URL that we're submitting as an article
	 */
	protected RichTextField urlField = new RichTextField();

	/**
	 * @member srField A Field to display the subreddit we're submitting to
	 */
	protected RichTextField srField = new RichTextField();
	
    /**
     * @member imgsubreds some sudreddits that we can submit pictures to
     */
    protected String imgsubreds[] = {"reddit.com", "pics",
			"funny", "itookapicture" };
	
    protected boolean captchaRequired = false;
    
	/**
	 * The screen that controls the submission of articles
	 */
	public SubmitScreen()
	{
		RichTextField submitField = new RichTextField(RichTextField.TEXT_ALIGN_HCENTER);
		submitField.setText("Submit an article:");
		submitField.setPadding(8, 0, 24, 0);
		
		this.add(submitField);
	}
	
	/**
	 * Pop up file selector dialog
	 */
	public void setupSubmit()
	{
		// File Selection
		FileSelectorPopupScreen fileSelector = new FileSelectorPopupScreen();
		fileSelector.pickFile();
		String fileName = fileSelector.getFile();
		
		// If a file has been selected
		if (fileName != null) 
		{
			// Add a slash to the front
			fileName = "/" + fileName;
			
			// Display a dialog for selecting a subreddit to submit to. 
			// This returns the index into the array of imgsubreds
			// If the user cancels, the value is -1.
			int index = Dialog.ask("To which subreddit?", imgsubreds, 0);
			
			if (index >= 0) 
			{	
				//get the subreddit we'll be submitting to from the array
				this.subreddit = imgsubreds[index];
				
				// Create Imgur object and submit photo.
				Imgur imgur = new Imgur(fileName);
				// Add listener for file loaded to imgur
				Event.observe(imgur, "LOADED", imgurListener);
				imgur.submitPhoto();
				
				// temporary override of the loading to imgur, so that we 
				// don't exceed our # of daily submissions
				//imgurListener.callback(null);
			}
		}

	}

	/**
	 * Listener for when the image has been submitted to Imgur
	 */
	public Listener imgurListener = new Listener()
	{
		public void callback(final Object r)
		{
			prepSubmission((Imgur) r);
		}
	};
	
	protected void prepSubmission(Imgur imgur)
	{
    	this.url = imgur.getImgurURL();
    	this.captchaRequired();
	}
	
	protected void captchaRequired()
	{
    	Hashtable postData = new Hashtable();
    	postData.put("uh", RedditSession.getInstance().getModHash());
    	
    	//Find out if we need a captcha
		HTTPRequestThread requestThread = new HTTPRequestThread
		(
				"http://www.reddit.com/api/submit", 
				postData
		);
	    Event.observe(requestThread, "LOADED", getCaptchaListener);
	    requestThread.start();
	}
	
	/**
	 * The Listener that lets us know if we need a captcha or not, and gives us the URL of it.
	 */
	public Listener getCaptchaListener = new Listener()
	{
		public void callback(final Object r)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
	    	{
	    		public void run()
	    		{
	    			buildUI(((HTTPRequestThread)r).getHTTPReponseText());
	    		}
	    	});
		}
	};
	/**
	 * @todo BAD_CAPTCHA constant
	 * @param response
	 */
	protected void buildUI(final String response)
	{
		
		JSONObject myData = null;
		
        // Process the JSONObject
        try
        {
        	myData = new JSONObject(response);
            // The data node of the JSON object
            JSONArray myArray = myData.getJSONArray("jquery");
            
            //boolean captchaNeeded = false;
         
            //The captcha is needed, if it tells you you failed it
            if(myArray.length() >= 12)
            {
            	JSONArray capNeededArray = myArray.getJSONArray(12);
            	capNeededArray = capNeededArray.getJSONArray(3);
            	
            	if(capNeededArray.getString(0).equals(".error.BAD_CAPTCHA.field-captcha"))
            	{
            		this.captchaRequired = true;
            	}
            }
            
            myArray = myArray.getJSONArray(10);
            myArray = myArray.getJSONArray(3);
            this.captchaID = myArray.get(0).toString();

    		titleField.setLabel("Title: ");
    		
    		ButtonField cancelButton = new ButtonField("Cancel", ButtonField.CONSUME_CLICK);
            cancelButton.setChangeListener(this.cancelListener);
            
    		ButtonField submitButton = new ButtonField("Submit", ButtonField.CONSUME_CLICK);
            submitButton.setChangeListener(this.submitListener);
      
            this.setURL(this.url);
            this.setSubreddit(this.subreddit);
            
            urlField.setPadding(4, 4, 4, 4);
            titleField.setPadding(4, 4, 4, 4);
            srField.setPadding(4, 4, 4, 4);
            
            this.add(titleField);
            
            if(this.captchaRequired)
            {
    			HTTPBitmapField captchaBitmap = new HTTPBitmapField("http://www.reddit.com/captcha/"+captchaID+".png");
        		captchaBitmap.setPadding(4, 4, 4, 4);
        		
        		captchaField.setLabel("Captcha: ");
        		captchaField.setPadding(4, 4, 4, 4);
        		
            	this.add(captchaField);
    			this.add(captchaBitmap);
            }
            
            HorizontalFieldManager buttonManager = new HorizontalFieldManager(Field.FIELD_HCENTER);
            buttonManager.add(cancelButton);
            buttonManager.add(submitButton);
            
            this.add(urlField);
            this.add(srField);
            this.add(buttonManager);
            		
        }
		catch (final Exception e)
		{

		}
	}
	
	
	/**
	 * Listener to let us know that the HTTPRequest to submit an article has returned.
	 */
	public static Listener articleSubmittedListener = new Listener()
	{
		public void callback(final Object r)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
	    	{
	    		public void run()
	    		{
	    			HTTPRequestThread thr = (HTTPRequestThread)r;
	    			String response = thr.getHTTPReponseText();
	    			
	    			JSONObject myData = null;
	    			try 
	    	        {
						myData = new JSONObject(response);

						JSONArray myArray;
						myArray = myData.getJSONArray("jquery");
						
						//the 18th index has the BAD_CAPTCHA stuff in it
						//or the URL of the newly submitted article
						myArray = myArray.getJSONArray(18);
						myArray = myArray.getJSONArray(3);
						String answer = myArray.get(0).toString();
						if(answer.equals(".error.BAD_CAPTCHA.field-captcha"))
						{
							Dialog.alert("Failed captcha! Please try again.");	
						}
						else
						{
							Dialog.alert("Article submitted to " + answer);
						}
					}
	    	        catch (Exception e)
	    	        {
	    	        	//fails
	    	        }
	    		}
	    	});
		}
	};

	
	/**
	 * Set the URL to be submitted
	 * 
	 * @param url URL we're submitting to reddit
	 */
	public void setURL(String url)
	{
		this.url = url;
		urlField.setText("URL: " + url);
	}
	/**
	 * Set the subreddit to submit to
	 * 
	 * @param sr The subreddit to submit to
	 */
	public void setSubreddit(String sr)
	{
		this.subreddit = sr;
		this.srField.setText("Subreddit: " + sr);
	}
	
	/**
	 * Listens for the cancel button to be pressed
	 */
	protected FieldChangeListener cancelListener = new FieldChangeListener()
	{	
		/** 
	 	* Method that's called the cancel button is pressed
	 	* 
	 	* @param field No idea what this does
	 	* @param context No idea what this does
	 	*/
		public void fieldChanged(Field field, int context)
		{
			UiApplication.getUiApplication().invokeLater(new Runnable()
	    	{
	    		public void run()
	    		{
	    			//Get rid of this screen
	    			UiApplication.getUiApplication().popScreen(
	    					UiApplication.getUiApplication().getActiveScreen());
	    		}
	    	});	
		}
	};

	/**
	 * Listens for the submit button to be pressed
	 * 
	 * @todo validate the length of the title. I'm sure Reddit limits it
	 */
	protected FieldChangeListener submitListener = new FieldChangeListener()
	{
		/**
	 	* Method that's called the login button is pressed
	 	* 
	 	* @param field No idea what this does
	 	* @param context No idea what this does
	 	*/
		public void fieldChanged(Field field, int context)
		{
			try
			{
			// Build post data for submitting an article
			Hashtable postData = new Hashtable();
			postData.put("uh", RedditSession.getInstance().getModHash());
			postData.put("kind", "link");
			postData.put("url", url);
			postData.put("sr", subreddit);
			
			// Get user title 
    		postData.put("title", titleField.getText());
			
			// Technically, even submitting a wrong captcha will 
			// succeed, if it wasn't required of the user, so no biggie.
    		if(captchaRequired)
    		{
    			postData.put("iden", captchaID);
    			postData.put("captcha", captchaField.getText());
    		}
    		
    		//Make a request thread with this info
    		HTTPRequestThread request = new HTTPRequestThread("http://www.reddit.com/api/submit", postData);
    		
    		// Attach event listener
    		Event.observe(request, "LOADED", articleSubmittedListener);
    		
    		request.start();
			}
			catch(final Exception e)
			{
				UiApplication.getUiApplication().invokeLater(new Runnable()
				{
					public void run()
					{
						Dialog.alert("Failed to submit: " + e.toString());
					}
				});
			}
			
    		UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
		}		
	};	
}
