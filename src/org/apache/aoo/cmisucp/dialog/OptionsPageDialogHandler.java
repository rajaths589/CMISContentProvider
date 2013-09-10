package org.apache.aoo.cmisucp.dialog;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.MessageBoxButtons;
import com.sun.star.awt.MessageBoxResults;
import com.sun.star.awt.MessageBoxType;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogProvider2;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XTextComponent;
import com.sun.star.awt.XWindow;
import com.sun.star.beans.PropertyState;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.deployment.PackageInformationProvider;
import com.sun.star.deployment.XPackageInformationProvider;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.task.UrlRecord;
import com.sun.star.task.XInteractionHandler;
import com.sun.star.task.XMasterPasswordHandling;
import com.sun.star.task.XPasswordContainer;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rajath
 */
public final class OptionsPageDialogHandler extends WeakBase
        implements com.sun.star.awt.XContainerWindowEventHandler, XServiceInfo {

    private final XComponentContext m_xContext;
    private static final String m_implementationName = OptionsPageDialogHandler.class.getName();
    private static final String[] m_serviceNames = {"org.apache.aoo.cmisucp.dialog.OptionsPageDialogHandler"};
    private XButton add;
    private XButton edit;
    private XButton delete;
    private XListBox urlList;
    private XListBox usernameList;
    private XPropertySet xPropSet;
    private XPasswordContainer xPass;
    private XInteractionHandler xHandler;
    private XMultiComponentFactory xMCF;
    private XWindow m_xWindow;

    /**
     *
     * @param context
     */
    public OptionsPageDialogHandler(XComponentContext context) {
        m_xContext = context;
        xMCF = context.getServiceManager();
        XMultiServiceFactory xMSF = null;
        try {
            xMSF = UnoRuntime.queryInterface(XMultiServiceFactory.class, xMCF.createInstanceWithContext("com.sun.star.configuration.ConfigurationProvider", context));
        } catch (com.sun.star.uno.Exception e) {
            Logger.getAnonymousLogger().info("not getting configuration access");
        }


        //    xMSF = UnoRuntime.queryInterface(XMultiServiceFactory.class, xMCF.createInstanceWithContext("com.sun.star.configuration.ConfigurationProvider", context));            
        Object args[] = new Object[1];
        PropertyValue configArg = new PropertyValue();
        configArg.Name = "nodepath";
        configArg.State = PropertyState.DIRECT_VALUE;
        configArg.Handle = 0;
        configArg.Value = "/org.apache.aoo.cmisucp.OptionsDialog/Data";
        args[0] = configArg;
        try {
            //xPropSet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xMSF.createInstanceWithArguments("com.sun.star.configuration.ConfigurationUpdateAccess", args));            
            //xPass = UnoRuntime.queryInterface(XPasswordContainer.class, xPropSet.getPropertyValue("URL-PasswordStore"));            
            Object passObj = xMCF.createInstanceWithContext("com.sun.star.task.PasswordContainer", context);
            xPass = (XPasswordContainer) UnoRuntime.queryInterface(XPasswordContainer.class, passObj);
            XMasterPasswordHandling xMPH = UnoRuntime.queryInterface(XMasterPasswordHandling.class, passObj);
            xMPH.allowPersistentStoring(true);
            //xHandler = (XInteractionHandler) xMCF.createInstanceWithContext("com.sun.star.task.InteractionHandler",context);            
            xHandler = UnoRuntime.queryInterface(XInteractionHandler.class, xMCF.createInstanceWithContext("com.sun.star.task.InteractionHandler", context));
        } catch (com.sun.star.uno.Exception ex) {
            Logger.getLogger(OptionsPageDialogHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OptionsPageDialogHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
        XSingleComponentFactory xFactory = null;

        if (sImplementationName.equals(m_implementationName)) {
            xFactory = Factory.createComponentFactory(OptionsPageDialogHandler.class, m_serviceNames);
        }
        return xFactory;
    }

    /**
     *
     * @param xRegistryKey
     * @return
     */
    public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                m_serviceNames,
                xRegistryKey);
    }

    // com.sun.star.awt.XContainerWindowEventHandler:
    public boolean callHandlerMethod(com.sun.star.awt.XWindow xWindow, Object EventObject, String MethodName) throws com.sun.star.lang.WrappedTargetException {
        if (MethodName.equals("external_event")) {
            try {
                return handleExternalEvent(xWindow, EventObject);
            } catch (Exception e) {
                Logger.getLogger(this.getImplementationName()).info(MethodName);
            }
        }
        return false;
    }

    private boolean handleExternalEvent(XWindow xWindow, Object EventObject) throws IllegalArgumentException, WrappedTargetException {
        String sMethod = AnyConverter.toString(EventObject);
        if (sMethod.equals("ok")) {
            return true;
        } else if (sMethod.equals("back") || sMethod.equals("initialize")) {
            return loadData(xWindow);
        }
        return false;
    }

    private boolean loadData(XWindow xWindow) {
        m_xWindow = xWindow;
        XControlContainer xControlContainer = (XControlContainer) UnoRuntime.queryInterface(XControlContainer.class, xWindow);

        add = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("add"));
        edit = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("edit"));
        delete = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("delete"));
        urlList = UnoRuntime.queryInterface(XListBox.class, xControlContainer.getControl("ListBox1"));
        usernameList = UnoRuntime.queryInterface(XListBox.class, xControlContainer.getControl("ListBox2"));
        urlList.setMultipleMode(false);

        add.setActionCommand("add");
        edit.setActionCommand("edit");
        delete.setActionCommand("delete");

        BtnClassListener listen = new BtnClassListener();
        add.addActionListener(listen);
        edit.addActionListener(listen);
        delete.addActionListener(listen);

        short n = 0;
        for (UrlRecord u : xPass.getAllPersistent(xHandler)) {
            if (u.Url.startsWith("cmis")) {
                urlList.addItem(u.Url, n);
                usernameList.addItem(u.UserList[0].UserName, n);
                n++;
            }
        }
        return true;
    }

    private void refreshTable() {
        urlList.removeItems((short) 0, (short) (urlList.getItemCount()));
        usernameList.removeItems((short) 0, (short) (usernameList.getItemCount()));
        short n = 0;

        for (UrlRecord u : xPass.getAllPersistent(xHandler)) {
            if (u.Url.startsWith("cmis")) {
                urlList.addItem(u.Url, n);
                usernameList.addItem(u.UserList[0].UserName, n);
                n++;
            }
        }
    }

    /**
     *
     * @return
     */
    public String[] getSupportedMethodNames() {
        return new String[]{"external_event"};
    }

    public String getImplementationName() {
        return m_implementationName;
    }

    /**
     *
     * @param arg0
     * @return
     */
    public boolean supportsService(String arg0) {
        int len = m_serviceNames.length;

        for (int i = 0; i < len; i++) {
            if (arg0.equals(m_serviceNames[i])) {
                return true;
            }
        }
        return false;
    }

    private void addItem() throws com.sun.star.uno.Exception {
        Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);
        XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
        XFrame current_frame = xDesktop.getCurrentFrame();
        Object dialogProvider = xMCF.createInstanceWithContext("com.sun.star.awt.DialogProvider2", m_xContext);
        XDialogProvider2 xDialogProvider2 = UnoRuntime.queryInterface(XDialogProvider2.class, dialogProvider);
        XPackageInformationProvider packageInfo = PackageInformationProvider.get(m_xContext);
        String location = packageInfo.getPackageLocation("org.apache.aoo.cmisucp.CMISContentProvider");
        String dialogURL = location + "/dialogs/addDialog.xdl";
        XDialog xDialog = xDialogProvider2.createDialog(dialogURL);
        XControlContainer xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, xDialog);
        XButton addBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton1"));
        XButton cancelBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton2"));
        XTextComponent userNamefield = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField2"));
        XTextComponent urlfield = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField1"));
        XTextComponent passwordfield = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField3"));
        addBtn.setActionCommand("addDialogAdd");
        cancelBtn.setActionCommand("addDialogCancel");
        BtnClassListener addDialogListener = new BtnClassListener(xDialog);
        addBtn.addActionListener(addDialogListener);
        cancelBtn.addActionListener(addDialogListener);
        xDialog.setTitle("ADD");
        xDialog.execute();
        if (addDialogListener.getAccepted()) {
            String url = urlfield.getText();
            if (url.startsWith("cmis")) {
                String username = userNamefield.getText();
                String password = passwordfield.getText();
                UrlRecord urlRec = xPass.find(url, xHandler);
                if (urlRec.Url.equals(url)) {
                    showErrorMessage("Error", "Only one username and password per person");
                    addItem();
                } else if (urlRec.Url.equals("")) {
                    xPass.addPersistent(url, username, new String[]{password}, xHandler);
                    refreshTable();
                } else {
                    XComponent xMessageComponent = null;
                    try {
                        Object oToolkit = xMCF.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext);
                        XMessageBoxFactory xMessageBoxFactory = (XMessageBoxFactory) UnoRuntime.queryInterface(XMessageBoxFactory.class, oToolkit);
                        Rectangle aRectangle = new Rectangle();
                        XControl windowControl = UnoRuntime.queryInterface(XControl.class, m_xWindow);
                        XMessageBox xMessage = xMessageBoxFactory.createMessageBox(windowControl.getPeer(), MessageBoxType.QUERYBOX, MessageBoxButtons.BUTTONS_YES_NO, "Similar URL Found", "URL: " + urlRec.Url + " found. Still add?");
                        xMessageComponent = UnoRuntime.queryInterface(XComponent.class, xMessage);
                        if (xMessage != null) {
                            short res = xMessage.execute();
                            if (res == MessageBoxResults.YES) {
                                xPass.addPersistent(url, username, new String[]{password}, xHandler);
                                refreshTable();
                            } else {
                                addItem();
                            }
                        }
                    } catch (com.sun.star.uno.Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (xMessageComponent != null) {
                            xMessageComponent.dispose();
                        }
                    }
                }
            } else {
                showErrorMessage("Error", "Add only cmis url");
                addItem();
            }
        }
    }

    private void showErrorMessage(String title, String Message) {
        XComponent xMessageComponent = null;
        try {
            Object oToolkit = xMCF.createInstanceWithContext("com.sun.star.awt.Toolkit", m_xContext);
            XMessageBoxFactory xMessageBoxFactory = (XMessageBoxFactory) UnoRuntime.queryInterface(XMessageBoxFactory.class, oToolkit);
            Rectangle aRectangle = new Rectangle();
            XControl windowControl = UnoRuntime.queryInterface(XControl.class, m_xWindow);
            XMessageBox xMessage = xMessageBoxFactory.createMessageBox(windowControl.getPeer(), MessageBoxType.ERRORBOX, MessageBoxButtons.BUTTONS_OK, title, Message);
            xMessageComponent = UnoRuntime.queryInterface(XComponent.class, xMessage);
            if (xMessage != null) {
                xMessage.execute();
            }
        } catch (com.sun.star.uno.Exception e) {
            e.printStackTrace();
        } finally {
            if (xMessageComponent != null) {
                xMessageComponent.dispose();
            }
        }
    }

    private void editItem() throws com.sun.star.uno.Exception {

        String url = urlList.getSelectedItem();
        if (url != null && !("".equals(url))) {
            String username = usernameList.getItem(urlList.getSelectedItemPos());
            UrlRecord urlRec = xPass.findForName(url, username, xHandler);
            Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);
            XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
            XFrame current_frame = xDesktop.getCurrentFrame();
            Object dialogProvider = xMCF.createInstanceWithContext("com.sun.star.awt.DialogProvider2", m_xContext);
            XDialogProvider2 xDialogProvider2 = UnoRuntime.queryInterface(XDialogProvider2.class, dialogProvider);
            XPackageInformationProvider packageInfo = PackageInformationProvider.get(m_xContext);
            String location = packageInfo.getPackageLocation("org.apache.aoo.cmisucp.CMISContentProvider");
            String dialogURL = location + "/dialogs/editDialog.xdl";
            XDialog xDialog = xDialogProvider2.createDialog(dialogURL);
            XControlContainer xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, xDialog);
            XButton addBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton1"));
            XButton cancelBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton2"));
            XTextComponent userNamefield = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField2"));
            XTextComponent urlfield = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField1"));
            XTextComponent passwordfield = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField3"));
            userNamefield.setText(username);
            userNamefield.setEditable(false);
            urlfield.setText(url);
            urlfield.setEditable(false);
            addBtn.setActionCommand("editDialogEdit");
            cancelBtn.setActionCommand("editDialogCancel");
            BtnClassListener editDialogListener = new BtnClassListener(xDialog);
            addBtn.addActionListener(editDialogListener);
            cancelBtn.addActionListener(editDialogListener);
            xDialog.setTitle("EDIT");
            xDialog.execute();
            if (editDialogListener.getAccepted()) {
                String password = passwordfield.getText();
                xPass.removePersistent(url, username);
                xPass.addPersistent(url, username, new String[]{password}, xHandler);
                refreshTable();
            }
        }
    }

    private void deleteItem() throws com.sun.star.uno.Exception {
        String url = urlList.getSelectedItem();
        if (url != null && !("".equals(url))) {
            String username = usernameList.getItem(urlList.getSelectedItemPos());
            Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);
            XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
            XFrame current_frame = xDesktop.getCurrentFrame();
            Object dialogProvider = xMCF.createInstanceWithContext("com.sun.star.awt.DialogProvider2", m_xContext);
            XDialogProvider2 xDialogProvider2 = UnoRuntime.queryInterface(XDialogProvider2.class, dialogProvider);
            XPackageInformationProvider packageInfo = PackageInformationProvider.get(m_xContext);
            String location = packageInfo.getPackageLocation("org.apache.aoo.cmisucp.CMISContentProvider");
            String dialogURL = location + "/dialogs/deleteDialog.xdl";
            XDialog xDialog = xDialogProvider2.createDialog(dialogURL);
            XControlContainer xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, xDialog);
            XButton addBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton1"));
            XButton cancelBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton2"));
            XTextComponent userNamefield = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField2"));
            XTextComponent urlfield = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField1"));
            userNamefield.setText(username);
            userNamefield.setEditable(false);
            urlfield.setText(url);
            urlfield.setEditable(false);
            addBtn.setActionCommand("deleteDialogDelete");
            cancelBtn.setActionCommand("deleteDialogCancel");
            BtnClassListener deleteDialogListener = new BtnClassListener(xDialog);
            addBtn.addActionListener(deleteDialogListener);
            cancelBtn.addActionListener(deleteDialogListener);
            xDialog.setTitle("DELETE");
            xDialog.execute();
            if (deleteDialogListener.getAccepted()) {
                xPass.removePersistent(url, username);
                refreshTable();
            }
        }
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    /**
     *
     */
    public class BtnClassListener implements XActionListener {

        private boolean accepted;
        private XDialog xD;

        public BtnClassListener(XDialog dialog) {
            xD = dialog;
        }

        /**
         *
         */
        public BtnClassListener() {
        }

        public void actionPerformed(ActionEvent arg0) {
            if ("add".equals(arg0.ActionCommand)) {
                try {
                    addItem();
                } catch (com.sun.star.uno.Exception ex) {
                    Logger.getLogger(OptionsPageDialogHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ("edit".equals(arg0.ActionCommand)) {
                try {
                    editItem();
                } catch (com.sun.star.uno.Exception ex) {
                    Logger.getLogger(OptionsPageDialogHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ("delete".equals(arg0.ActionCommand)) {
                try {
                    deleteItem();
                } catch (com.sun.star.uno.Exception ex) {
                    Logger.getLogger(OptionsPageDialogHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if ("addDialogAdd".equals(arg0.ActionCommand)) {
                accepted = true;
                xD.endExecute();
            } else if ("addDialogCancel".equals(arg0.ActionCommand)) {
                accepted = false;
                xD.endExecute();
            } else if ("editDialogEdit".equals(arg0.ActionCommand)) {
                accepted = true;
                xD.endExecute();
            } else if ("editDialogCancel".equals(arg0.ActionCommand)) {
                accepted = false;
                xD.endExecute();
            } else if ("deleteDialogDelete".equals(arg0.ActionCommand)) {
                accepted = true;
                xD.endExecute();
            } else if ("deleteDialogCancel".equals(arg0.ActionCommand)) {
                accepted = false;
                xD.endExecute();
            }
        }

        /**
         *
         * @return
         */
        public boolean getAccepted() {
            return accepted;
        }

        /**
         *
         * @param arg0
         */
        public void disposing(EventObject arg0) {
            xD = null;
        }
    }
}
