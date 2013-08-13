package org.apache.aoo.cmisucp.dialog;

import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyState;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.WrappedTargetRuntimeException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;


public final class OptionsDialogHandler extends WeakBase
   implements com.sun.star.awt.XContainerWindowEventHandler
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = OptionsDialogHandler.class.getName();
    private final XMultiComponentFactory m_xMCF;
    private final XPropertySet m_xPropOptions;
    // Controls in the Window
    private String m_Window = "CMISUCPOptionsPage";
    private String m_Controls[] = {"head","ListBox1","ListBox2","url","user","edit","delete","add"};
    
    public OptionsDialogHandler( XComponentContext context )
    {
        m_xContext = context;
        m_xMCF = m_xContext.getServiceManager();
        
        XMultiServiceFactory xConfig;
        try
        {
            xConfig = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, m_xMCF.createInstanceWithContext("com.sun.star.configuration.ConfigurationProvider", context));            
            Object args[] = new Object[1];
            args[0] = new PropertyValue("nodepath", 0, "/org.apache.aoo.cmisucp.OptionsDialog/Data", PropertyState.DIRECT_VALUE);
            m_xPropOptions = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xConfig.createInstanceWithArguments("com.sun.star.configuration.ConfigurationUpdateAccess", args));
        }
        catch(com.sun.star.uno.Exception ex)
        {
            throw new WrappedTargetRuntimeException("Could not obtain XMusltiServiceFactory");
        }   
    };

    // com.sun.star.awt.XContainerWindowEventHandler:
    public boolean callHandlerMethod(com.sun.star.awt.XWindow xWindow, Object EventObject, String MethodName) throws com.sun.star.lang.WrappedTargetException
    {        
        if(MethodName.equals("external_event"))
        {
            try
            {
                return handleExternalEvent(xWindow, EventObject);
            }
            catch(Exception e)
            {
                
            }
        }
        return false;
    }
    private boolean handleExternalEvent(XWindow xWindow, Object EventObject) throws com.sun.star.uno.Exception
    {
        try
        {
            String sMethod = AnyConverter.toString(EventObject);
            if(sMethod.equals("ok"))
            {
                saveData(xWindow);
            }
            else if(sMethod.equals("back") || sMethod.equals("initialize"))
            {
                loadData(xWindow);
            }
            return true;
        }
        catch(com.sun.star.lang.IllegalArgumentException ex)
        {
            ex.printStackTrace();
        }
        return false;
    }
    
    private void loadData(XWindow aWindow) throws com.sun.star.uno.Exception
    {
        XControlContainer xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, aWindow);
        if(xControlContainer==null)
            throw new com.sun.star.uno.Exception("Could not get Control Container from WIndow");                              
    }
    
    private void saveData(XWindow aWindow)
    {
        
    }
    public String[] getSupportedMethodNames()
    {        
        return new String[] { "external_event" };
    }

}
