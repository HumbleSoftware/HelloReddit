package com.humblesoftware.reddit;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;

/**
 * HTTPBitmapField
 * 
 * A field to get a bitmap from a URL asynchronously.
 */
public class HTTPBitmapField extends BitmapField
{
	protected EncodedImage bitmap = null;
	protected String url = null;

	public static Vector list = new Vector();
	public static int count = 0;
	public static Object stick = new Object();
	
	public HTTPBitmapField(String url)
	{
		super(new Bitmap(1,1), Manager.FOCUSABLE);
		this.url = url;
		synchronized(HTTPBitmapField.stick)
		{
			if (this.count >= 2)
			{
				HTTPBitmapField.list.addElement(this);
				return;
			}
			else
			{
				this.count++;
				this.request();
			}
		}
	}
	
	public HTTPBitmapField(String url, int style)
	{
		super(new Bitmap(1,1), Manager.FOCUSABLE|style);
		this.url = url;
		synchronized(HTTPBitmapField.stick)
		{
			if (this.count >= 2)
			{
				HTTPBitmapField.list.addElement(this);
				return;
			}
			else
			{
				this.count++;
				this.request();
			}
		}
	}
	
	protected boolean navigationClick(int status, int time)
	{
		return true;
	}
	
	public void request()
	{
		HTTPRequestThread requestThread = new HTTPRequestThread(this.url);
		Event.observe(requestThread, "LOADED", this.requestListener);
	    requestThread.start(); 
	}
	
	public Bitmap getBitmap()
	{
		if (this.bitmap == null)
		{
			return null;
		}
		else
		{
			return bitmap.getBitmap();
		}
	}
	
	public Listener requestListener = new Listener()
	{
    	public void callback(Object r)
    	{
    		loadContent(((HTTPRequestThread) r).getHTTPReponseText());
    	}
	};
	
	public void loadContent(String responseText) 
	{
		// Process queue
		HTTPBitmapField f = null;
		synchronized(HTTPBitmapField.stick)
		{
			if (!HTTPBitmapField.list.isEmpty())
			{
				f = (HTTPBitmapField) HTTPBitmapField.list.firstElement();
				HTTPBitmapField.list.removeElement(f);
			}
			else
			{
				HTTPBitmapField.count--;
			}
		}
		if (f != null) 
		{
			f.request();
		}
		
		// Built bitmap from request response text.
		try
		{
			byte[] dataArray = responseText.getBytes();
			this.bitmap = EncodedImage.createEncodedImage(
					dataArray,0,dataArray.length);
			UiApplication.getUiApplication().invokeLater(new Runnable()
			{
				public void run()
				{
					setImage(bitmap);
				}
			});
		}
		catch (Exception e)
		{
		}
	}
}