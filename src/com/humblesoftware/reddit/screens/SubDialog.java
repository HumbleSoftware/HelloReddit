package com.humblesoftware.reddit.screens;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.DialogFieldManager;

final class SubDialog extends Dialog
{

	private EditField subReddit;
	private ButtonField okButton;
	private ButtonField cancelButton;
	
	public SubDialog(String choices[], int values[])
	{
	    super("go to subreddit:", choices,values,Dialog.OK, Bitmap.getPredefinedBitmap(Bitmap.QUESTION), Dialog.GLOBAL_STATUS);
	    subReddit = new EditField("subreddit: ", "", 50, EditField.EDITABLE);
	    net.rim.device.api.ui.Manager delegate = getDelegate();
	    
	    if ( delegate instanceof DialogFieldManager)
	    {
	        DialogFieldManager dfm = (DialogFieldManager)delegate;
	        net.rim.device.api.ui.Manager manager =dfm.getCustomManager();
	        
	        if ( manager != null )
	        {
	            manager.insert(subReddit, 0);
	        }
	    }
	}    
	
	public String getSubreddit()
	{
		return subReddit.getText();
	}

}