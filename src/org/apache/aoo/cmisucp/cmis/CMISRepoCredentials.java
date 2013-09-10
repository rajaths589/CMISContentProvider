/**
 * ************************************************************
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 ************************************************************
 */
package org.apache.aoo.cmisucp.cmis;

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
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XTextComponent;
import com.sun.star.deployment.PackageInformationProvider;
import com.sun.star.deployment.XPackageInformationProvider;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.task.UrlRecord;
import com.sun.star.task.XInteractionHandler;
import com.sun.star.task.XMasterPasswordHandling;
import com.sun.star.task.XPasswordContainer;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

/**
 *
 * @author rajath
 */
public class CMISRepoCredentials {

    public final String URL;
    private final String username;
    private final String password;
    /**
     *
     */
    public String serverURL;
    private XPasswordContainer xPass;
    private XInteractionHandler handler;
    private String matchURL;

    public CMISRepoCredentials(String url, XInteractionHandler xIH, XComponentContext context) {
        URL = url;
        String arr[] = obtainUserFromPasswordContainer(xIH, context, url);
        username = arr[0];
        password = arr[1];
    }

    /**
     *
     * @param url
     * @param user
     * @param pwd
     */
    public CMISRepoCredentials(String url, String user, String pwd) {
        URL = url;
        password = pwd;
        username = user;
    }

    private String[] obtainUserFromPasswordContainer(XInteractionHandler xIH, XComponentContext context, String url) {
        xPass = null;
        handler = xIH;
        XMasterPasswordHandling xMaster = null;

        if (xIH == null || context == null) {
            throw new IllegalArgumentException("InteractionHandler or Component Context is Null");
        }

        XMultiComponentFactory factory = context.getServiceManager();
        if (factory != null) {
            XFrame current_frame = null;
            try {
                Object passObj = factory.createInstanceWithContext("com.sun.star.task.PasswordContainer", context);
                xPass = UnoRuntime.queryInterface(XPasswordContainer.class, passObj);
                xMaster = UnoRuntime.queryInterface(XMasterPasswordHandling.class, passObj);
                Object desktop = factory.createInstanceWithContext("com.sun.star.frame.Desktop", context);
                XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
                current_frame = xDesktop.getCurrentFrame();
            } catch (Exception ex) {
                // log the exception
            }

            if (xPass != null && xMaster != null) {
                xMaster.allowPersistentStoring(true);
                UrlRecord urlMatch = xPass.find(url, xIH);
                if (url.equals(urlMatch.Url)) {
                    matchURL = urlMatch.Url;
                    return new String[]{urlMatch.UserList[0].UserName, urlMatch.UserList[0].Passwords[0]};
                } else if (!urlMatch.Url.equals("")) {
                    String possibleMatch = urlMatch.Url;
                    if (urlMatch.Url.endsWith("/")) {
                        possibleMatch = urlMatch.Url.substring(0, urlMatch.Url.length() - 1);
                    }
                    if (urlMatch.Url.endsWith("/.")) {
                        possibleMatch = urlMatch.Url.substring(0, urlMatch.Url.length() - 2);
                    }

                    if (url.startsWith(possibleMatch)) {
                        matchURL = urlMatch.Url;
                        return new String[]{urlMatch.UserList[0].UserName, urlMatch.UserList[0].Passwords[0]};
                    }
                    XComponent xMessageComp = null;
                    try {
                        Object toolkit = factory.createInstanceWithContext("com.sun.star.awt.Toolkit", context);
                        XMessageBoxFactory xMessageBoxFactory = (XMessageBoxFactory) UnoRuntime.queryInterface(XMessageBoxFactory.class, toolkit);
                        Rectangle aRectangle = new Rectangle();
                        XControl windowControl = UnoRuntime.queryInterface(XControl.class, current_frame.getContainerWindow());
                        XMessageBox xMessage;
                        if (windowControl != null) {
                            xMessage = xMessageBoxFactory.createMessageBox(windowControl.getPeer(), MessageBoxType.QUERYBOX, MessageBoxButtons.BUTTONS_YES_NO, "Similar URL Found", "URL: " + urlMatch.Url + " is similar to the entered url. Use same user data?");
                        } else {
                            xMessage = xMessageBoxFactory.createMessageBox(null, MessageBoxType.QUERYBOX, MessageBoxButtons.BUTTONS_YES_NO, "Similar URL Found", "URL: " + urlMatch.Url + " is similar to the entered url. Use same user data?");
                        }

                        if (xMessage != null) {
                            short result = xMessage.execute();
                            if (result == MessageBoxResults.YES) {
                                return new String[]{urlMatch.UserList[0].UserName, urlMatch.UserList[0].Passwords[0]};
                            } else {
                                return getPassword(context, factory, current_frame);
                            }
                        }
                    } catch (com.sun.star.uno.Exception ex) {
                        // log the message
                    }
                } else {
                    return getPassword(context, factory, current_frame);
                }
            }
        }

        return null;
    }

    private String[] getPassword(XComponentContext xContext, XMultiComponentFactory factory, XFrame frame) {
        try {
            XPackageInformationProvider infoProvider = PackageInformationProvider.get(xContext);
            String xdlLoc = infoProvider.getPackageLocation("org.apache.aoo.cmisucp.CMISContentProvider") + "/dialogs/PasswordDialog.xdl";
            Object dialogProvider = factory.createInstanceWithContext("com.sun.star.awt.DialogProvider2", xContext);
            XDialogProvider2 xDialogProvider2 = UnoRuntime.queryInterface(XDialogProvider2.class, dialogProvider);
            XDialog passwordDialog = xDialogProvider2.createDialog(xdlLoc);
            XControlContainer xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, passwordDialog);
            XFixedText urlText = UnoRuntime.queryInterface(XFixedText.class, xControlContainer.getControl("Label1"));
            XTextComponent usernameText = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField1"));
            XTextComponent passwordText = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField2"));
            XButton saveBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton1"));
            XButton cancelBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton2"));
            saveBtn.setActionCommand("save");
            cancelBtn.setActionCommand("cancel");
            PassDlgBtnListener passDlgBtnListener = new PassDlgBtnListener(passwordDialog);
            saveBtn.addActionListener(passDlgBtnListener);
            cancelBtn.addActionListener(passDlgBtnListener);
            urlText.setText(URL);
            passwordDialog.execute();
            if (passDlgBtnListener.getValue()) {
                //   username = usernameText.getText();            
                //   password = passwordText.getText();
                return new String[]{usernameText.getText(), passwordText.getText()};
            }
        } catch (com.sun.star.uno.Exception e) {
        }
        return new String[0];
    }

    public CMISConnect getConnect(XComponentContext xContent) {
        CMISConnect connect;

        if (matchURL == null) {
            connect = new CMISConnect(xContent, URL, username, password);
        } else {
            connect = new CMISConnect(xContent, URL, username, password, matchURL);
        }

        boolean authStatus = connect.getAuthStatus();
        if (authStatus == true && matchURL == null) {
            saveAsPersistent();
        }
        setServerURL(connect.getRepositoryURL());
        return connect;
    }

    private void saveAsPersistent() {
        xPass.addPersistent(URL, username, new String[]{password}, handler);
    }

    public void setServerURL(String url) {
        serverURL = url;
    }

    /**
     *
     * @param compare
     * @return
     */
    public boolean startsWithServer(CMISRepoCredentials compare) {
        if (compare.serverURL != null) {
            if ((URL.startsWith(compare.serverURL))) {
                return true;
            }
        }
        return false;
    }

    public CMISRepoCredentials createChildCredentials(String childName) {
        if (URL.endsWith("/")) {
            return new CMISRepoCredentials(URL + childName, username, password);
        } else {
            return new CMISRepoCredentials(URL + "/" + childName, username, password);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CMISRepoCredentials) {
            CMISRepoCredentials cred = (CMISRepoCredentials) o;
            if (cred.URL.equals(URL) && cred.username.equals(username) && cred.password.equals(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.URL != null ? this.URL.hashCode() : 0);
        hash = 97 * hash + (this.username != null ? this.username.hashCode() : 0);
        hash = 61 * hash + (this.password != null ? this.password.hashCode() : 0);
        return hash;
    }

    public class PassDlgBtnListener implements XActionListener {

        boolean value = false;
        XDialog xD;

        public PassDlgBtnListener(XDialog dia) {
            xD = dia;
        }

        public void actionPerformed(ActionEvent arg0) {
            if (arg0.ActionCommand.equalsIgnoreCase("save")) {
                value = true;
            }
            xD.endExecute();
        }

        public boolean getValue() {
            return value;
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
