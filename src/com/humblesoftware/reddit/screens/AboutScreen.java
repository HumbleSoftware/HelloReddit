package com.humblesoftware.reddit.screens;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.ActiveRichTextField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.VerticalFieldManager;


/**
 * About Screen
 */
public class AboutScreen extends HelloRedditScreenBase
{
    public AboutScreen()
    {
        super();
        
        // Setup fields
        RichTextField titleField = new RichTextField(RichTextField.TEXT_ALIGN_HCENTER);
        titleField.setText("HelloReddit");
        titleField.setFont(Font.getDefault().derive(0, 32));
        titleField.setPadding(0,0,12,0);
        
        RichTextField byField = new RichTextField(RichTextField.TEXT_ALIGN_HCENTER);
        byField.setText("by:  humble software development");
        byField.setFont(Font.getDefault().derive(0, 16));
        byField.setPadding(0,0,2,0);
        
        ActiveRichTextField linkField = new ActiveRichTextField("http://www.humblesoftware.com/helloreddit/", ActiveRichTextField.TEXT_ALIGN_HCENTER);
        linkField.setFont(Font.getDefault().derive(0, 16));
        linkField.setPadding(0,0,2,0);
        
        RichTextField copyField = new RichTextField(RichTextField.TEXT_ALIGN_HCENTER);
        copyField.setText("\u00a9 2010 h.s.d.");
        copyField.setFont(Font.getDefault().derive(0, 16));
        
        
        // Build an interior manager
        VerticalFieldManager aboutManager = new VerticalFieldManager(VerticalFieldManager.FIELD_VCENTER);
        
        aboutManager.add(titleField);
        aboutManager.add(byField);
        aboutManager.add(linkField);
        aboutManager.add(copyField);
        
        int topPadding = this.getCenter() - (aboutManager.getPreferredHeight() / 2);
        aboutManager.setPadding(topPadding, 0, 0, 0);
        
        this.add(aboutManager);
    }
}