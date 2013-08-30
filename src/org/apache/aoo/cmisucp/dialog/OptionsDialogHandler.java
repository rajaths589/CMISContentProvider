package org.apache.aoo.cmisucp.dialog;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XControlModel;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyState;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.WrappedTargetRuntimeException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.task.UrlRecord;
import com.sun.star.task.XInteractionHandler;
import com.sun.star.task.XPasswordContainer;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class OptionsDialogHandler extends WeakBase
   implements com.sun.star.awt.XContainerWindowEventHandler
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = OptionsDialogHandler.class.getName();
    private final XMultiComponentFactory m_xMCF;
    private final XPropertySet m_xPropOptions;
    private XControlContainer xControlContainer;
    private XPasswordContainer m_xPasswordContainer;
    private XInteractionHandler m_xHandler;
    private XMultiServiceFactory xConfig;
    
    // Controls in the Window
    private XWindow window;
    private String m_Window = "CMISUCPOptionsPage";
    private String m_Controls[] = {"head","ListBox1","ListBox2","url","user","edit","delete","add"};
    private XListBox xListBox1;
    private XListBox xListBox2;
    private XButton xAdd;
    private XButton xEdit;
    private XButton xDelete;
    private XTextComponent dURL;
    private XTextComponent dUser;
    private XTextComponent dPass;
    private String URL;
    
    public OptionsDialogHandler( XComponentContext context )
    {
        m_xContext = context;
        m_xMCF = m_xContext.getServiceManager();        
        try
        {
            xConfig = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, m_xMCF.createInstanceWithContext("com.sun.star.configuration.ConfigurationProvider", context));            
            Object args[] = new Object[1];
            args[0] = new PropertyValue("nodepath", 0, "/org.apache.aoo.cmisucp.OptionsDialog/Data", PropertyState.DIRECT_VALUE);
            m_xPropOptions = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xConfig.createInstanceWithArguments("com.sun.star.configuration.ConfigurationUpdateAccess", args));
            //m_xPasswordContainer = (XPasswordContainer) xConfig.createInstance("com.sun.star.task.PasswordContainer");
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
        window = xWindow;
        try
        {
            String sMethod = AnyConverter.toString(EventObject);
            if(sMethod.equals("ok"))
            {
                // to-do
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
        xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, aWindow);
        if(xControlContainer==null)
            throw new com.sun.star.uno.Exception("Could not get Control Container from WIndow");                              
        
        XControl listbox1 = xControlContainer.getControl("ListBox1");
        XControl listbox2 = xControlContainer.getControl("ListBox2");
        XControl add = xControlContainer.getControl("add");
        XControl edit = xControlContainer.getControl("edit");
        XControl delete = xControlContainer.getControl("delete");        
        xListBox1 = UnoRuntime.queryInterface(XListBox.class, listbox1);    
        xListBox2 = UnoRuntime.queryInterface(XListBox.class, listbox2);
        xAdd = UnoRuntime.queryInterface(XButton.class, add);
        xEdit = UnoRuntime.queryInterface(XButton.class, edit);
        xDelete = UnoRuntime.queryInterface(XButton.class, delete);
        xAdd.setActionCommand("add pressed");
        xEdit.setActionCommand("edit pressed");
        xDelete.setActionCommand("delete pressed");
        
        ButtonActionListener actionListener = new ButtonActionListener();
        xAdd.addActionListener(actionListener);
        xEdit.addActionListener(actionListener);
        xDelete.addActionListener(actionListener);
        
        m_xPasswordContainer = UnoRuntime.queryInterface(XPasswordContainer.class,m_xPropOptions.getPropertyValue("passwordContainer"));
        m_xHandler = (XInteractionHandler) xConfig.createInstance("com.sun.star.task.InteractionHandler");
        
        if(m_xPasswordContainer!=null)
        {
            UrlRecord urlmap[] = m_xPasswordContainer.getAllPersistent(m_xHandler);
            for(UrlRecord rec:urlmap)
            {
                xListBox1.addItem(rec.Url, (short) (xListBox1.getItemCount()+1));                        
                xListBox2.addItem(rec.UserList[0].UserName, (short) (xListBox2.getItemCount()+1));
            }                
        }    
        
        xListBox1.setMultipleMode(false);
        xListBox2.setMultipleMode(false);
    }        
    
    private void deleteData()            
    {
        if(xListBox1!=null && xListBox2!=null && m_xPasswordContainer!=null)
        {
            short s = xListBox1.getSelectedItemPos();                
            m_xPasswordContainer.removePersistent(xListBox1.getItem(s), xListBox2.getItem(s));
            xListBox1.removeItems(s, (short)1);
            xListBox2.removeItems(s, (short)1);        
        }
    }
    public String[] getSupportedMethodNames()
    {        
        return new String[] { "external_event" };
    }

    public class ButtonActionListener implements XActionListener{

        public void actionPerformed(ActionEvent arg0) {
            if(arg0.ActionCommand.equalsIgnoreCase("add pressed"))
            {
                try 
                {
                    Object dialogModel = m_xMCF.createInstanceWithContext("com.sun.star.awt.UnoControlDialogModel", m_xContext);
                    XPropertySet xPSet = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, dialogModel); 
                    xPSet.setPropertyValue("Title", "Edit");
                    xPSet.setPropertyValue("Height", new Integer(240));
                    xPSet.setPropertyValue("Width", new Integer(320));
                    xPSet.setPropertyValue("PositionX", new Integer(100));
                    xPSet.setPropertyValue("PositionY", new Integer(100));
                    xPSet.setPropertyValue("Closeable", true);
                    xPSet.setPropertyValue("Moveable", true);                                        
                    xPSet.setPropertyValue("Sizeable", true);   
                    
                    XMultiServiceFactory xMSF = UnoRuntime.queryInterface(XMultiServiceFactory.class, dialogModel);
                    
                    Object urlLabel = xMSF.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
                    XPropertySet urlLabelProp = UnoRuntime.queryInterface(XPropertySet.class, urlLabel);
                    urlLabelProp.setPropertyValue("PositionX", new Integer(20));
                    urlLabelProp.setPropertyValue("PositionY", new Integer(30));
                    urlLabelProp.setPropertyValue("Height", new Integer(20));
                    urlLabelProp.setPropertyValue("Width", new Integer(20));                    
                    urlLabelProp.setPropertyValue("Label", "URL:");
                    urlLabelProp.setPropertyValue("Name", "urlLabel");
                    
                    
                    Object urlEdit = xMSF.createInstance("com.sun.star.awt.UnoContolEditModel");
                    XPropertySet urlEditProp = UnoRuntime.queryInterface(XPropertySet.class, urlEdit);
                    urlEditProp.setPropertyValue("PositionX", new Integer(60));
                    urlEditProp.setPropertyValue("PositionY", new Integer(30));
                    urlEditProp.setPropertyValue("Height", new Integer(20));
                    urlEditProp.setPropertyValue("Width", new Integer(150));
                    urlEditProp.setPropertyValue("AutoHScroll", true);
                    urlEditProp.setPropertyValue("MultiLine", false);
                    urlEditProp.setPropertyValue("ReadOnly", false);
                    URL = xListBox1.getSelectedItem();
                    urlEditProp.setPropertyValue("Text", "");
                    urlEditProp.setPropertyValue("Name", "urlEdit");
                    
                    Object userLabel = xMSF.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
                    XPropertySet userLabelProp = UnoRuntime.queryInterface(XPropertySet.class, userLabel);
                    userLabelProp.setPropertyValue("PositionX", new Integer(20));
                    userLabelProp.setPropertyValue("PositionY", new Integer(70));
                    userLabelProp.setPropertyValue("Height", new Integer(20));
                    userLabelProp.setPropertyValue("Width", new Integer(20));                    
                    userLabelProp.setPropertyValue("Label", "Username:");
                    userLabelProp.setPropertyValue("Name", "userLabel");
                    
                    Object userEdit = xMSF.createInstance("com.sun.star.awt.UnoContolEditModel");
                    XPropertySet userEditProp = UnoRuntime.queryInterface(XPropertySet.class, userEdit);
                    userEditProp.setPropertyValue("PositionX", new Integer(60));
                    userEditProp.setPropertyValue("PositionY", new Integer(70));
                    userEditProp.setPropertyValue("Height", new Integer(20));
                    userEditProp.setPropertyValue("Width", new Integer(150));
                    userEditProp.setPropertyValue("AutoHScroll", true);
                    userEditProp.setPropertyValue("MultiLine", false);
                    userEditProp.setPropertyValue("ReadOnly", false);
                    userEditProp.setPropertyValue("Text", "");
                    userEditProp.setPropertyValue("Name", "userEdit");
                    
                    Object pwdLabel = xMSF.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
                    XPropertySet pwdLabelProp = UnoRuntime.queryInterface(XPropertySet.class, userLabel);
                    pwdLabelProp.setPropertyValue("PositionX", new Integer(20));
                    pwdLabelProp.setPropertyValue("PositionY", new Integer(120));
                    pwdLabelProp.setPropertyValue("Height", new Integer(20));
                    pwdLabelProp.setPropertyValue("Width", new Integer(20));                    
                    pwdLabelProp.setPropertyValue("Label", "Password:");
                    pwdLabelProp.setPropertyValue("Name", "pwdLabel");
                    
                    Object pwdEdit = xMSF.createInstance("com.sun.star.awt.UnoContolEditModel");
                    XPropertySet pwdEditProp = UnoRuntime.queryInterface(XPropertySet.class, urlEdit);
                    pwdEditProp.setPropertyValue("PositionX", new Integer(60));
                    pwdEditProp.setPropertyValue("PositionY", new Integer(120));
                    pwdEditProp.setPropertyValue("Height", new Integer(20));
                    pwdEditProp.setPropertyValue("Width", new Integer(150));                    
                    pwdEditProp.setPropertyValue("MultiLine", false);
                    pwdEditProp.setPropertyValue("ReadOnly", false);
                    pwdEditProp.setPropertyValue("Text", "");
                    pwdEditProp.setPropertyValue("EchoChar", (short)'*');
                    pwdEditProp.setPropertyValue("Name", "pwdEdit");                    
                    
                    Object saveButton = xMSF.createInstance("com.sun.star.awt.UnoControlButtonModel");
                    XPropertySet saveBtnProp = UnoRuntime.queryInterface(XPropertySet.class, saveButton);
                    saveBtnProp.setPropertyValue("PositionX", new Integer(50));
                    saveBtnProp.setPropertyValue("PositionY", new Integer(200));
                    saveBtnProp.setPropertyValue("Height", new Integer(20));
                    saveBtnProp.setPropertyValue("Width", new Integer(20));
                    saveBtnProp.setPropertyValue("Label", "Save");
                    saveBtnProp.setPropertyValue("Name", "saveBtn");
                    
                    XNameContainer xName = UnoRuntime.queryInterface(XNameContainer.class, dialogModel);
                    xName.insertByName("saveBtn", saveButton);
                    xName.insertByName("pwdEdit", pwdEdit);
                    xName.insertByName("pwdLabel", pwdLabel);
                    xName.insertByName("userEdit", userEdit);
                    xName.insertByName("userLabel", userLabel);
                    xName.insertByName("urlLabel", urlLabel);
                    xName.insertByName("urlEdit", urlEdit);
                                        
                    Object dialog = m_xMCF.createInstanceWithContext("com.sun.star.awt.UnoDialogControl", m_xContext);
                    XControl dControl = UnoRuntime.queryInterface(XControl.class, dialog);                    
                    XControlModel dControlModel = UnoRuntime.queryInterface(XControlModel.class, dialogModel);
                    dControl.setModel(dControlModel);
                    
                    XControlContainer dControlContainer = UnoRuntime.queryInterface(XControlContainer.class, dialog);                   
                    XButton xButton = UnoRuntime.queryInterface(XButton.class, dControlContainer.getControl("saveBtn"));                    
                    xButton.setActionCommand("saveBtn pressed");                    
                    dUser = UnoRuntime.queryInterface(XTextComponent.class, dControlContainer.getControl("userEdit"));
                    dPass = UnoRuntime.queryInterface(XTextComponent.class, dControlContainer.getControl("pwdEdit"));
                    dURL = UnoRuntime.queryInterface(XTextComponent.class, dControlContainer.getControl("urlEdit"));
                    xButton.addActionListener(new DialogBtnListener());                                        
                    
                    Object toolkit = m_xMCF.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext);
                    XToolkit xToolkit = (XToolkit)UnoRuntime.queryInterface(XToolkit.class, toolkit);
                    XWindow xWindow = UnoRuntime.queryInterface(XWindow.class, dControl);
                    xWindow.setVisible(true);
                    dControl.createPeer(xToolkit, null);
                    XDialog xDialog = (XDialog)UnoRuntime.queryInterface(XDialog.class, dialog);
                    xDialog.execute();
                }
                catch (com.sun.star.uno.Exception ex) 
                {
                    Logger.getLogger(OptionsDialogHandler.class.getName()).log(Level.SEVERE, null, ex);
                }            
            }
            else if(arg0.ActionCommand.equalsIgnoreCase("delete pressed"))
            {
                deleteData();                
            }
            else if(arg0.ActionCommand.equalsIgnoreCase("edit pressed"))
            {                
                try 
                {
                    Object dialogModel = m_xMCF.createInstanceWithContext("com.sun.star.awt.UnoControlDialogModel", m_xContext);
                    XPropertySet xPSet = (XPropertySet)UnoRuntime.queryInterface(XPropertySet.class, dialogModel); 
                    xPSet.setPropertyValue("Title", "Edit");
                    xPSet.setPropertyValue("Height", new Integer(240));
                    xPSet.setPropertyValue("Width", new Integer(320));
                    xPSet.setPropertyValue("PositionX", new Integer(100));
                    xPSet.setPropertyValue("PositionY", new Integer(100));
                    xPSet.setPropertyValue("Closeable", true);
                    xPSet.setPropertyValue("Moveable", true);                                        
                    xPSet.setPropertyValue("Sizeable", true);   
                    
                    XMultiServiceFactory xMSF = UnoRuntime.queryInterface(XMultiServiceFactory.class, dialogModel);
                    
                    Object urlLabel = xMSF.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
                    XPropertySet urlLabelProp = UnoRuntime.queryInterface(XPropertySet.class, urlLabel);
                    urlLabelProp.setPropertyValue("PositionX", new Integer(20));
                    urlLabelProp.setPropertyValue("PositionY", new Integer(30));
                    urlLabelProp.setPropertyValue("Height", new Integer(20));
                    urlLabelProp.setPropertyValue("Width", new Integer(20));                    
                    urlLabelProp.setPropertyValue("Label", "URL:");
                    urlLabelProp.setPropertyValue("Name", "urlLabel");
                    
                    
                    Object urlEdit = xMSF.createInstance("com.sun.star.awt.UnoContolEditModel");
                    XPropertySet urlEditProp = UnoRuntime.queryInterface(XPropertySet.class, urlEdit);
                    urlEditProp.setPropertyValue("PositionX", new Integer(60));
                    urlEditProp.setPropertyValue("PositionY", new Integer(30));
                    urlEditProp.setPropertyValue("Height", new Integer(20));
                    urlEditProp.setPropertyValue("Width", new Integer(150));
                    urlEditProp.setPropertyValue("AutoHScroll", true);
                    urlEditProp.setPropertyValue("MultiLine", false);
                    urlEditProp.setPropertyValue("ReadOnly", true);                    
                    urlEditProp.setPropertyValue("Text", xListBox1.getSelectedItem());
                    urlEditProp.setPropertyValue("Name", "urlEdit");
                    
                    Object userLabel = xMSF.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
                    XPropertySet userLabelProp = UnoRuntime.queryInterface(XPropertySet.class, userLabel);
                    userLabelProp.setPropertyValue("PositionX", new Integer(20));
                    userLabelProp.setPropertyValue("PositionY", new Integer(70));
                    userLabelProp.setPropertyValue("Height", new Integer(20));
                    userLabelProp.setPropertyValue("Width", new Integer(20));                    
                    userLabelProp.setPropertyValue("Label", "Username:");
                    userLabelProp.setPropertyValue("Name", "userLabel");
                    
                    Object userEdit = xMSF.createInstance("com.sun.star.awt.UnoContolEditModel");
                    XPropertySet userEditProp = UnoRuntime.queryInterface(XPropertySet.class, urlEdit);
                    userEditProp.setPropertyValue("PositionX", new Integer(60));
                    userEditProp.setPropertyValue("PositionY", new Integer(70));
                    userEditProp.setPropertyValue("Height", new Integer(20));
                    userEditProp.setPropertyValue("Width", new Integer(150));
                    userEditProp.setPropertyValue("AutoHScroll", true);
                    userEditProp.setPropertyValue("MultiLine", false);
                    userEditProp.setPropertyValue("ReadOnly", false);
                    userEditProp.setPropertyValue("Text", xListBox2.getItem(xListBox1.getSelectedItemPos()));
                    userEditProp.setPropertyValue("Name", "userEdit");
                    
                    Object pwdLabel = xMSF.createInstance("com.sun.star.awt.UnoControlFixedTextModel");
                    XPropertySet pwdLabelProp = UnoRuntime.queryInterface(XPropertySet.class, userLabel);
                    pwdLabelProp.setPropertyValue("PositionX", new Integer(20));
                    pwdLabelProp.setPropertyValue("PositionY", new Integer(120));
                    pwdLabelProp.setPropertyValue("Height", new Integer(20));
                    pwdLabelProp.setPropertyValue("Width", new Integer(20));                    
                    pwdLabelProp.setPropertyValue("Label", "Password:");
                    pwdLabelProp.setPropertyValue("Name", "pwdLabel");
                    
                    Object pwdEdit = xMSF.createInstance("com.sun.star.awt.UnoContolEditModel");
                    XPropertySet pwdEditProp = UnoRuntime.queryInterface(XPropertySet.class, urlEdit);
                    pwdEditProp.setPropertyValue("PositionX", new Integer(60));
                    pwdEditProp.setPropertyValue("PositionY", new Integer(120));
                    pwdEditProp.setPropertyValue("Height", new Integer(20));
                    pwdEditProp.setPropertyValue("Width", new Integer(150));                    
                    pwdEditProp.setPropertyValue("MultiLine", false);
                    pwdEditProp.setPropertyValue("ReadOnly", false);
                    pwdEditProp.setPropertyValue("Text", "");
                    pwdEditProp.setPropertyValue("EchoChar", (short)'*');
                    pwdEditProp.setPropertyValue("Name", "pwdEdit");                    
                    
                    Object saveButton = xMSF.createInstance("com.sun.star.awt.UnoControlButtonModel");
                    XPropertySet saveBtnProp = UnoRuntime.queryInterface(XPropertySet.class, saveButton);
                    saveBtnProp.setPropertyValue("PositionX", new Integer(50));
                    saveBtnProp.setPropertyValue("PositionY", new Integer(200));
                    saveBtnProp.setPropertyValue("Height", new Integer(20));
                    saveBtnProp.setPropertyValue("Width", new Integer(20));
                    saveBtnProp.setPropertyValue("Label", "Save");
                    saveBtnProp.setPropertyValue("Name", "saveBtn");
                    
                    XNameContainer xName = UnoRuntime.queryInterface(XNameContainer.class, dialogModel);
                    xName.insertByName("saveBtn", saveButton);
                    xName.insertByName("pwdEdit", pwdEdit);
                    xName.insertByName("pwdLabel", pwdLabel);
                    xName.insertByName("userEdit", userEdit);
                    xName.insertByName("userLabel", userLabel);
                    xName.insertByName("urlLabel", urlLabel);
                    xName.insertByName("urlEdit", urlEdit);
                                        
                    Object dialog = m_xMCF.createInstanceWithContext("com.sun.star.awt.UnoDialogControl", m_xContext);
                    XControl dControl = UnoRuntime.queryInterface(XControl.class, dialog);                    
                    XControlModel dControlModel = UnoRuntime.queryInterface(XControlModel.class, dialogModel);
                    dControl.setModel(dControlModel);
                    
                    XControlContainer dControlContainer = UnoRuntime.queryInterface(XControlContainer.class, dialog);                   
                    XButton xButton = UnoRuntime.queryInterface(XButton.class, dControlContainer.getControl("saveBtn"));                    
                    xButton.setActionCommand("saveBtn pressed");                    
                    dUser = UnoRuntime.queryInterface(XTextComponent.class, dControlContainer.getControl("userEdit"));
                    dPass = UnoRuntime.queryInterface(XTextComponent.class, dControlContainer.getControl("pwdEdit"));
                    dURL = UnoRuntime.queryInterface(XTextComponent.class, dControlContainer.getControl("urlEdit"));
                    xButton.addActionListener(new DialogBtnListener());                                        
                    
                    Object toolkit = m_xMCF.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext);
                    XToolkit xToolkit = (XToolkit)UnoRuntime.queryInterface(XToolkit.class, toolkit);
                    XWindow xWindow = UnoRuntime.queryInterface(XWindow.class, dControl);
                    xWindow.setVisible(true);
                    dControl.createPeer(xToolkit, null);
                    XDialog xDialog = (XDialog)UnoRuntime.queryInterface(XDialog.class, dialog);
                    xDialog.execute();
                }
                catch (com.sun.star.uno.Exception ex) 
                {
                    Logger.getLogger(OptionsDialogHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }   

        public void disposing(EventObject arg0) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    public class DialogBtnListener implements XActionListener
    {

        public void actionPerformed(ActionEvent arg0) {
            if(arg0.ActionCommand.equalsIgnoreCase("saveBtn pressed"))
            {                                    
                String user = dUser.getText();
                String passwd = dUser.getText();                
                m_xPasswordContainer.removePersistent(URL, dUser.getText());
                m_xPasswordContainer.addPersistent(URL, dUser.getText(), new String[]{dPass.getText()}, m_xHandler);                               
            }
        }

        public void disposing(EventObject arg0) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
