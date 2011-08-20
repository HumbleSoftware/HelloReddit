package com.humblesoftware.imgur;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.io.Base64OutputStream;
/**
 * Class that handles image files
 */
public class ImageModel 
{
	/**
	 * This method takes a path to an image, in the form of /SDCard/location/image.jpg
	 * and returns a Base64 encoded String of the image
	 * 
	 * @param pathToImage
	 * @return a Base64 encoded String of the image
	 */
	public static String ImageToBase64(String pathToImage)
	{
		String retVal = "";
		
		try {
			FileConnection conn = (FileConnection) Connector.open( "file://" + pathToImage,
					Connector.READ );
			
			InputStreamReader isr = new InputStreamReader(conn.openInputStream());
		    
			byte[] toEncode = new byte[(int) conn.fileSize()];
		    
		    for(int i = 0; i<conn.fileSize(); ++i)
			{
		    	toEncode[i] = (byte) isr.read();
			}
		    
		    int offset = 0;
		    byte[] encoded = Base64OutputStream.encode(toEncode, offset, toEncode.length, false, false);
		    
		    retVal = new String(encoded, "UTF-8");
		    conn.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// FAILED
		}
				
		return retVal;
	}
	
	
}
