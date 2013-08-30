package org.apache.aoo.cmisucp.dialog;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XDialog;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class CheckoutDialogHandler extends WeakBase
   implements com.sun.star.awt.XDialogEventHandler
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CheckoutDialogHandler.class.getName();
    private XDialog m_xDialog;
    private boolean btnPressed = false;
    private boolean accept = false;
    
    public CheckoutDialogHandler( XComponentContext context )
    {        
        m_xContext = context;
        btnPressed = false;
        accept = false;
    };

    // com.sun.star.awt.XDialogEventHandler:
    public boolean callHandlerMethod(com.sun.star.awt.XDialog xDialog, Object EventObject, String MethodName) throws com.sun.star.lang.WrappedTargetException
    {        
        if(MethodName.equalsIgnoreCase("external_event"))
        {
            try {
                return handleExternalEvent(xDialog, EventObject);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(CheckoutDialogHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    private boolean handleExternalEvent(XDialog xDialog, Object EventObject) throws IllegalArgumentException
    {
        String sMethod = AnyConverter.toString(EventObject);
        if(sMethod.equalsIgnoreCase("back")||sMethod.equalsIgnoreCase("initialize"))
        {
            loadData(xDialog);
            return true;
        }
        else 
        {
            return false;
        }
    }
    private void loadData(XDialog xDialog)
    {
        m_xDialog = xDialog;
        XControlContainer xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, xDialog);
        XControl yesControl = xControlContainer.getControl("CommandButton1");
        XControl cancelControl = xControlContainer.getControl("CommandButton2");
        XButton yesButton = UnoRuntime.queryInterface(XButton.class, yesControl);
        yesButton.setActionCommand("yes");
        XButton cancelButton = UnoRuntime.queryInterface(XButton.class, cancelControl);
        cancelButton.setActionCommand("cancel");
        BtnActionListener actionListener = new BtnActionListener();                
        yesButton.addActionListener(actionListener);
        cancelButton.addActionListener(actionListener);
    }   
        
    public String[] getSupportedMethodNames()
    {     
        return new String[] {"external_event"};
    }
    
    public boolean getButtonPressed()
    {
        while(btnPressed!=true)
        {
            ;
        }
        return accept;
    }
    public class BtnActionListener implements XActionListener
    {

        public void actionPerformed(ActionEvent arg0) 
        {
            if(arg0.ActionCommand.equalsIgnoreCase("yes"))
            {
                if(btnPressed==false)
                    btnPressed = true;
                accept = true;
                m_xDialog.endExecute();
            }
            else if(arg0.ActionCommand.equalsIgnoreCase("cancel"))
            {
                if(btnPressed==false)
                    btnPressed = true;
                accept = false;
                m_xDialog.endExecute();
            }
        }

        public void disposing(EventObject arg0) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }   
}

