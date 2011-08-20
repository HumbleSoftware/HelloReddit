package com.humblesoftware.reddit.screens;

/*
 * FileSelectorPopupScreen.java
 *
 * © Research In Motion, 2003-2009
 * Confidential and proprietary.
 */

import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.DialogFieldManager;
import javax.microedition.io.Connector;
import javax.microedition.io.file.*;
import java.util.*;

/**
 * A PopupScreen with a file browser allowing for file selection.
  */
public class FileSelectorPopupScreen extends PopupScreen
{

	String _currentPath;		//The current path;
	String[] _extensions;       //File extensions to filter by.
    ObjectListField _olf;       //Lists fields and directories.
    
    /**
     * Open the screen to the root folder and show all files and directories.
     */
    public FileSelectorPopupScreen() 
    {    
        this(null, null);
    }
    
    /**
     * Open the screen starting at the specified path and filter results based on 
     * a list of extensions.
     * @param startPath The initial path to open.  Use null to start at the root file systems.
     * @param extensions Allowable file extensions to display.  Use null to display all file types. 
     */
    public FileSelectorPopupScreen(String startPath, String[] extensions)
    {
        super(new DialogFieldManager());
        _extensions = extensions;
        prepScreen(startPath);
    }
    
    /**
     * Display the screen, prompting the user to pick a file.
     */
    public void pickFile()
    {
        UiApplication.getUiApplication().pushModalScreen(this);
    }
    
    /**
     * Retrieves the current directory if the user is still browsing for a file, 
     * the selected file if the user has chosen one or null if the user dismissed the screen.
     * @return the current directory if the user is still browsing for a file, 
     * the selected file if the user has chosen one or null if the user dismissed the screen.
     */
    public String getFile()
    {
        return _currentPath;
    }    
    
    //Prepare the DialogFieldManager.
    private void prepScreen(String path)
    {
        DialogFieldManager dfm = (DialogFieldManager)getDelegate();
        dfm.setIcon(new BitmapField(Bitmap.getPredefinedBitmap(Bitmap.QUESTION)));
        dfm.setMessage(new RichTextField("Select a file"));

        _olf = new ObjectListField();
        dfm.addCustomField(_olf);
        
        updateList(path);
    }
    
    //Reads all of the files and directories in a given path.
    private Vector readFiles(String path)
    {
        Enumeration fileEnum;
        Vector filesVector = new Vector();
        
        _currentPath = path;
        
        if (path == null)
        {
            //Read the file system roots.
            fileEnum = FileSystemRegistry.listRoots();
            
            while (fileEnum.hasMoreElements()) 
            {
            	filesVector.addElement((Object)fileEnum.nextElement());
            }
        }
        else
        {
            //Read the files and directories for the current path.
        	try
        	{
        		FileConnection fc = (FileConnection)Connector.open("file:///" + path);
        		fileEnum = fc.list();
        		String currentFile;
        		
                while (fileEnum.hasMoreElements()) 
                {
                	//Use the file extension filter, if there is one.
                	if (_extensions == null)
                	{
                		
                		filesVector.addElement((Object)fileEnum.nextElement());
                	}
                	else
                	{
                		currentFile = ((String)fileEnum.nextElement());
                	
                		if (currentFile.lastIndexOf('/') == (currentFile.length() - 1))
        				{
                			//Add all directories.
                			filesVector.addElement((Object)currentFile);
        				}
                		else
                		{
                			//This is a file.  Check if its extension matches the filter.
                			for (int count = _extensions.length - 1; count >= 0; --count)
                			{
                				if (currentFile.indexOf(_extensions[count]) != -1)
                				{
                					//There was a match, add the file and stop looping.
                					filesVector.addElement((Object)currentFile);
                					break;
                				}
                			}
                		}
                	}
                }
        	}
        	catch (Exception ex)
        	{
        		Dialog.alert("Unable to open folder. " + ex.toString());
        	}
        	
        }
        return filesVector;
    }
    
    //Handles a user picking an entry in the ObjectListField.
    private void doSelection()
    {
		//Determine the current path.
		String thePath = buildPath();

		if (thePath == null)
		{
			//Only update the screen if a directory was selected.
			updateList(thePath);
		}
		else if (!thePath.equals("*?*"))
		{
			//Only update the screen if a directory was selected.
			//A second check is required here to avoid a NullPointerException.
			updateList(thePath);
		}
		else
		{
			//The user has selected a file.
    		//Close the screen.
			this.close();
		}
    }
    
    //Updates the entries in the ObjectListField.
    private void updateList(String path)
    {
    	//Read all files and directories in the path.
    	Vector fileList = readFiles(path);
    	
    	//Create an array from the Vector.
    	Object fileArray[] = vectorToArray(fileList);
    	
    	//Update the field with the new files.
    	_olf.set(fileArray);
    }
    
    //Build a String that contains the full path of the user's selection.
    //If a file has been selected, close this screen.
    //Returns *?* if the user has selected a file.
    private String buildPath()
    {
    	
		String newPath = (String)_olf.get(_olf, _olf.getSelectedIndex());
    	
    	if (newPath.equals(".."))
    	{
    		//Go up a directory.
    		//Remove the trailing '/';
    		newPath = _currentPath.substring(0, _currentPath.length() - 2);
    		
    		//Remove everything after the last '/' (the current directory).
			//If a '/' is not found, the user is opening the file system roots.
			//Return null to cause the screen to display the file system roots.
			int lastSlash = newPath.lastIndexOf('/');
			
			if (lastSlash == -1)
			{
				newPath = null;
			}
			else
			{
				newPath = newPath.substring(0, lastSlash + 1);
			}
    	}
    	else if (newPath.lastIndexOf('/') == (newPath.length() - 1))
    	{
    		//If the path ends with /, a directory was selected.
    		//Prefix the _currentPath if it is not null (not in the root directory).
    		if (_currentPath != null)
    		{
    			newPath = _currentPath + newPath;
    		}
    	}
    	else
    	{
    		//A file was selected.
    		_currentPath += newPath;
    		
    		//Return *?* to stop the screen update process.
    		newPath = "*?*";
    	}
    	
    	return newPath;
    }
    
    //Saves the files and directories listed in vector format into an object array.
    private Object[] vectorToArray(Vector filesVector)
    {
        int filesCount = filesVector.size();
        int dotIncrementor;
        Object[] files;
        
        //If not in the root, add ".." to the top of the array. 
        if (_currentPath == null)
        {
        	dotIncrementor = 0;
        	files = new Object[(filesCount)];
        }
        else
        {
        	dotIncrementor = 1;
        	files = new Object[(filesCount + dotIncrementor)];

        	//Add .. at the top to go back a directory.
        	files[0] = (Object)("..");
        }
        
        for (int count = 0; count < filesCount; ++count)
        {
            files[count + dotIncrementor] = (Object)filesVector.elementAt(count);
        }
        
        return files;
    }    
    
    //Handle trackball clicks.  
    protected boolean navigationClick(int status, int time)
    {
    	doSelection();
    	return true;
    }
    
    protected boolean keyChar(char c, int status, int time)
    {
    	//Close this screen if escape is selected.
    	if (c == Characters.ESCAPE)
    	{
    		_currentPath = null;
    		this.close();
    		return true;
    	}
    	else if (c == Characters.ENTER)
    	{
    		doSelection();
    		return true;
    	}
    	
    	return super.keyChar(c, status, time);
    }
} 