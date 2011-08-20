package com.humblesoftware.reddit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.HttpsConnection;

import net.rim.blackberry.api.browser.URLEncodedPostData;

import java.util.Hashtable;

public class HTTPRequest
{
    /**
     * @member Latest HTTP response text
     */
    protected String HTTPResponseText = "";
    /**
     * @member Latest URL requested
     */
    protected String url = null;
    /**
     * @member UserAgent class constant.
     */
    protected String userAgent = "Profile/MIDP-2.0 Configuration/CLDC-1.0";
    /**
     * @member Parameters for get or post.
     */
    protected Hashtable params = null;
    /**
     * @member Method bits.
     */
    protected long method;
    /**
     * @member Post bytes.
     */
    protected byte[] post = null;
    /**
     * @member Set cookie response if any.
     */
    protected String setCookie = null;
    
    // Constants
    public static long METHOD_GET = 1;
    public static long METHOD_POST = 3;
    public static long METHOD_FORM_MULTIPART = 7;
    public static long METHOD_HTTPS = 8;
    
    protected HttpConnection connection = null;
    protected InputStream inputstream = null;
    protected OutputStream outputstream = null;
    
    protected byte[] file;
    protected String fileField;
    protected String fileName;
    protected String fileType;
    
    public HTTPRequest(String url)
    {
    	this.url = url;
    	this.method = HTTPRequest.METHOD_GET;
    	this.params = new Hashtable();
    }
    
    public HTTPRequest(String url, Hashtable params)
    {
    	this.url = url;
    	this.method = HTTPRequest.METHOD_POST;
    	this.params = params;
    }
    
    public HTTPRequest(String url, Hashtable params, long method)
    {
    	this.url = url;
    	this.method = method;
    	this.params = params;
    }
    
    public HTTPRequest(String url, Hashtable params, byte[] file, String fileField, String fileName, String fileType)
    {
    	this.url = url;
    	this.method = HTTPRequest.METHOD_FORM_MULTIPART;
    	this.params = params;
    	this.file = file;
    	this.fileField = fileField;
    	this.fileName = fileName;
    	this.fileType = fileType;
    }
    
    public String getSetCookie()
    {
    	return (this.setCookie == null) ? "" : this.setCookie;
    }
    
    /**
     * @todo Add a cookie thing so that this isn't tied to the reddit session 
     * @todo Check for 404 codes
     * @return
     */
    public boolean request()
    {
    	boolean success = true;
    	
    	try
        {
    		// Make HTTP Connection, HTTP or HTTPS
        	if (this.isHTTPS())
        	{
        		this.connection = (HttpsConnection) Connector.open(this.url);
        	}
        	else
        	{
        		this.connection = (HttpConnection) Connector.open(this.url);
        	}
        	
        	// Set user agent and connection properties.
            this.connection.setRequestProperty("User-Agent", this.userAgent);
            this.connection.setRequestProperty("Connection", "close");
    		
            // Set HTTP Request Method, GET or POST
        	if (this.isPOST())
        	{
        		this.connection.setRequestMethod(HttpConnection.POST);
        		
        		// Set URL Encoded or Form Multipart
        		if (this.isMULTIPART())
        		{
                    this.connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.getBoundaryString());
                    
                    this.buildFormMultipartPost();
        		}
        		else
        		{
        			this.connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        			
    			    this.buildURLEncodedPost();
        		}
        		
        		// Cookie
    			if (RedditSession.getInstance().isLoggedIn())
                {
                	connection.setRequestProperty("Cookie", RedditSession.getInstance().getCookie());
                }
    			else
    			{
    				this.connection.setRequestProperty("Cookie", "");
    			}
    			
    			// Post Output
    			outputstream = connection.openOutputStream();
                outputstream.write(this.post);
                outputstream.flush();
                
                //success = this.response();
        	}
        	else
        	{
        		this.connection.setRequestMethod(HttpConnection.GET);
                this.connection.setRequestProperty("Content-Type", "//text plain");
                
                // Cookie
    			if (RedditSession.getInstance().isLoggedIn())
                {
                	this.connection.setRequestProperty("Cookie", RedditSession.getInstance().getCookie());
                }
    			else
    			{
    				this.connection.setRequestProperty("Cookie", "");
    			}
        	}
        	
        	success = this.response();
        	
        	// Get Set-Cookie response parameter
        	String key;
        	for (int i = 0;(key = this.connection.getHeaderFieldKey(i)) != null; i++)
            {
                if (key.equalsIgnoreCase("set-cookie"))
                {
                    this.setCookie = this.connection.getHeaderField(key);
                } 
            }
        	
            // Close output stream if opened.
            if (this.isPOST())
            {
                outputstream.close();
            }
            
            this.connection.close();
        }
        catch (final Exception error)
        {
        	// Error handler
        }
        
    	return success;
    }
    
    /**
     * Handle an HTTP request response.
     * 
     * @return boolean  Success or failure.
     * @throws IOException 
     */
    protected boolean response() throws IOException
    {
    	boolean success;
    	
    	// Check response code for success
    	if (this.connection.getResponseCode() == HttpConnection.HTTP_OK)
        {
    		// Read response and set http response text
    		this.inputstream = connection.openInputStream();
            
            int length = (int) connection.getLength();
            if (length > 0)
            {
                byte incomingData[] = new byte[length];
                this.inputstream.read(incomingData);
                this.HTTPResponseText = new String(incomingData);
            }
            else
            {
                ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
                int ch;
                while ((ch = inputstream.read()) != -1)
                {
                    bytestream.write(ch);
                }
                this.HTTPResponseText = new String(bytestream.toByteArray());
                bytestream.close();
            }
            
            this.inputstream.close();
            
            success = true;
        }
        else
        {
            success = false;
        }

    	return success;
    }
    
    protected void buildURLEncodedPost()
    {
		URLEncodedPostData postData = new URLEncodedPostData(URLEncodedPostData.DEFAULT_CHARSET, false);
    	Enumeration keys = this.params.keys();
    	
		// Loop through parameters
    	while (keys.hasMoreElements())
    	{
    		String key = (String) keys.nextElement();
    		String value = (String) this.params.get(key);
    		
    		postData.append(key, value);
    	}
    	
    	this.post = postData.getBytes();
    }
    
    protected void buildFormMultipartPost() throws IOException
    {
    	String postData = "--" + this.getBoundaryString() + "\r\n";
    	
        Enumeration keys = this.params.keys();
        
        while (keys.hasMoreElements())
        {
        	String key = (String) keys.nextElement();
        	String value = (String) this.params.get(key);
        	
        	postData += "Content-Disposition: form-data; name=\"" + key + "\"\r\n";
        	postData += "\r\n" + value + "\r\n";
        	postData += "--" + this.getBoundaryString() + "\r\n";
        }
        
        postData += "Content-Disposition: form-data; name=\"" + fileField + "\"; filename=\"" + fileName + "\"\r\n";
        postData += "Content-Type: " + fileType + "\r\n\r\n";
        
        String endBoundary = "\r\n--" + this.getBoundaryString() + "--\r\n";
        
        ByteArrayOutputStream postBytes = new ByteArrayOutputStream();
	        
        postBytes.write(postData.getBytes());
        postBytes.write(this.file);
        postBytes.write(endBoundary.getBytes());
	    
        this.post = postBytes.toByteArray();
        postBytes.close();
    }
    
    protected String getBoundaryMessage(String boundary, Hashtable params, String fileField, String fileName, String fileType)
    {
        StringBuffer res = new StringBuffer("--").append(boundary).append("\r\n");
 
        Enumeration keys = params.keys();
 
        while(keys.hasMoreElements())
        {
            String key = (String)keys.nextElement();
            String value = (String)params.get(key);
 
            res.append("Content-Disposition: form-data; name=\"").append(key).append("\"\r\n")    
                .append("\r\n").append(value).append("\r\n")
                .append("--").append(boundary).append("\r\n");
        }
        res.append("Content-Disposition: form-data; name=\"").append(fileField).append("\"; filename=\"").append(fileName).append("\"\r\n") 
            .append("Content-Type: ").append(fileType).append("\r\n\r\n");
 
        return res.toString();
    }
    
    /**
     * Gets response text of the HTTP request.
     * 
     * @return HTTPResponseText
     */
    public String getHTTPReponseText()
    {
        return this.HTTPResponseText;
    }
    /**
     * Gets parameters submitted with request.
     * 
     * @return
     */
    public Hashtable getParams()
    {
    	return this.params;
    }
    /**
     * Gets the URL requested.
     * 
     * @return
     */
    public String getURL()
    {
    	return this.url;
    }
    
    /**
     * Checks if the HTTPS bit is set in the method. 
     * 
     * @return  True if the method should use HTTPS
     */
    protected boolean isHTTPS()
    {
    	if ((this.method & HTTPRequest.METHOD_HTTPS) == HTTPRequest.METHOD_HTTPS)
    	{
    		return true;
    	}
    	
    	return false;
    }
    /**
     * Checks if the POST bit is set in the method.
     * 
     * @return True if the method should use POST
     */
    protected boolean isPOST()
    {
    	if ((this.method & HTTPRequest.METHOD_POST) == HTTPRequest.METHOD_POST)
    	{
    		return true;
    	}
    	
    	return false;
    }
    /**
     * Checks if the Form/MultiPart bit is set in the method.
     * 
     * @return True if the method should use MULTIPART
     */
    protected boolean isMULTIPART()
    {
    	if ((this.method & HTTPRequest.METHOD_FORM_MULTIPART) == HTTPRequest.METHOD_FORM_MULTIPART)
    	{
    		return true;
    	}
    	
    	return false;
    }
    
    protected String getBoundaryString()
    {
    	return "----------v3UpaFg13e3bPJ9Zfh7aj6";
    }
}
