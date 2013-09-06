/**************************************************************
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 *************************************************************/
package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.awt.ActionEvent;
import com.sun.star.awt.XActionListener;
import com.sun.star.awt.XButton;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogProvider2;
import com.sun.star.awt.XRadioButton;
import com.sun.star.awt.XTextComponent;
import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyAttribute;
import com.sun.star.beans.PropertyChangeEvent;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertiesChangeListener;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.deployment.PackageInformationProvider;
import com.sun.star.deployment.XPackageInformationProvider;
import com.sun.star.frame.FrameSearchFlag;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.IOException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.io.XActiveDataSink;
import com.sun.star.io.XActiveDataStreamer;
import com.sun.star.io.XInputStream;
import com.sun.star.io.XOutputStream;
import com.sun.star.io.XStream;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.ComponentBase;
import com.sun.star.sdbc.XResultSet;
import com.sun.star.sdbc.XRow;
import com.sun.star.task.XInteractionHandler;
import com.sun.star.ucb.Command;
import com.sun.star.ucb.CommandFailedException;
import com.sun.star.ucb.ContentAction;
import com.sun.star.ucb.ContentCreationException;
import com.sun.star.ucb.ContentEvent;
import com.sun.star.ucb.ContentInfo;
import com.sun.star.ucb.ContentInfoAttribute;
import com.sun.star.ucb.InsertCommandArgument;
import com.sun.star.ucb.InteractiveBadTransferURLException;
import com.sun.star.ucb.OpenCommandArgument2;
import com.sun.star.ucb.OpenMode;
import com.sun.star.ucb.TransferInfo;
import com.sun.star.ucb.UnsupportedCommandException;
import com.sun.star.ucb.XCommandEnvironment;
import com.sun.star.ucb.XCommandInfo;
import com.sun.star.ucb.XCommandProcessor;
import com.sun.star.ucb.XContent;
import com.sun.star.ucb.XContentEventListener;
import com.sun.star.ucb.XContentIdentifier;
import com.sun.star.ucb.XContentIdentifierFactory;
import com.sun.star.ucb.XContentProvider;
import com.sun.star.ucb.XDynamicResultSet;
import com.sun.star.uno.Any;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.Type;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.aoo.cmisucp.CMISConstants;
import org.apache.aoo.cmisucp.cmis.CMISConnect;
import org.apache.aoo.cmisucp.cmis.CMISRepoCredentials;
import org.apache.aoo.cmisucp.cmis.CMISResourceCache;
import org.apache.aoo.cmisucp.cmis.CMISResourceManager;
import org.apache.aoo.cmisucp.helper.CheckinCommandArgument;
import org.apache.aoo.cmisucp.helper.ContentPropertySet;
import org.apache.aoo.cmisucp.helper.PropertyAndValueSet;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.util.FileUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

public final class CMISContent extends ComponentBase
        implements com.sun.star.beans.XPropertySetInfoChangeNotifier,
        com.sun.star.beans.XPropertiesChangeNotifier,
        com.sun.star.container.XChild,
        com.sun.star.ucb.XContent,
        com.sun.star.ucb.XCommandInfoChangeNotifier,
        com.sun.star.ucb.XCommandProcessor,
        com.sun.star.lang.XServiceInfo,
        com.sun.star.ucb.XCommandProcessor2,
        com.sun.star.ucb.XContentCreator,
        com.sun.star.beans.XPropertyContainer {

    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISContent.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.ucb.Content"};
    
    //My variables
    private XContentIdentifier xContentid;
    private CmisObject cmisContent;
    private Session connected_session;
    private String contentType;
    private static final Logger log = Logger.getLogger(CMISConnect.class.getName());
    private boolean is_folder;
    private String id;

    private boolean exists;     //Whether exists on CMIS repository
    private boolean created;    // whether created using createNewContent
    private boolean nameSet;    // whether name set
    private boolean inserted;   // whether inserted
    
    private String path;
    CMISResourceManager resourceManager;

    private List<XContentEventListener> contentEventListeners = new ArrayList<XContentEventListener>();    
    private Map<XPropertiesChangeListener,List<String>> propertiesChangeListeners = new HashMap<XPropertiesChangeListener,List<String>>();
    private CMISConnect aConnect;
    
    //UCB
    private XMultiComponentFactory xmcf;
    private XInterface xUCB;
    private XContentIdentifierFactory xContentIdentifierFactory;
    private XContentProvider xContentProvider;
    private boolean ucbInitialized = false;
    
    public CMISContent(XComponentContext context, XContentIdentifier xContentIdentifier) throws ContentCreationException, NotConnectedException, IOException  {
                
        m_xContext = context;
        xContentid = xContentIdentifier;        

        processIdentifier(xContentIdentifier.getContentIdentifier());
        path = xContentIdentifier.getContentIdentifier();
    
        created = false;
        nameSet = false;
        inserted = false;
        
    }

    public CMISContent(XComponentContext context, String type, String uri) throws ContentCreationException, NotConnectedException, IOException
    {        
        m_xContext = context;
        contentType = type;
        
        if(type.equalsIgnoreCase("application/cmis-document"))
            is_folder = false;
        else
            is_folder = true;
        
        processIdentifier(uri);
        exists = false;        
        created = true;
        inserted = false;
        nameSet = false;
        
        path = uri;
    }
    
    //My method
    public void processIdentifier(String uri) throws ContentCreationException, NotConnectedException, IOException {                
        try
        {     
            CMISRepoCredentials cred = new CMISRepoCredentials(uri, "rajaths589", "******");            
            //aConnect = new CMISConnect(m_xContext, uri, "rajaths589", "*****");
            //connected_session = aConnect.getSession();
            //cmisContent = aConnect.getObject();
            CMISResourceCache resCache = CMISResourceCache.getObject(m_xContext);
            resourceManager = resCache.getManager(cred);            
        }
        catch(CmisBaseException e)
        {
            exists = false;            
            if(e.getExceptionName().equalsIgnoreCase(CmisConnectionException.EXCEPTION_NAME))
            {
                throw new NotConnectedException("Cannot connect to URI:"+uri);                
            }
            else if(e.getExceptionName().equalsIgnoreCase(CmisObjectNotFoundException.EXCEPTION_NAME))
            {                                    
                throw new IOException("object not found");
            }
            else
            {
                // throw some expception
            }
            throw new ContentCreationException();  
        }
        exists = true;
        setContentType();
        // cache properties
    }

    private void setContentType()
    {
        if(!exists)
            return;
                
        contentType = resourceManager.getContentType();        
        id = resourceManager.getID();
        is_folder = resourceManager.isFolder();
    }
    
    public boolean isFolder() {
        return is_folder;
    }

    public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
        XSingleComponentFactory xFactory = null;

        if (sImplementationName.equals(m_implementationName)) {
            xFactory = Factory.createComponentFactory(CMISContent.class, m_serviceNames);
        }
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                m_serviceNames,
                xRegistryKey);
    }

    // com.sun.star.beans.XPropertySetInfoChangeNotifier:
    public void addPropertySetInfoChangeListener(com.sun.star.beans.XPropertySetInfoChangeListener Listener) {        
        throw new UnsupportedOperationException();
    }

    public void removePropertySetInfoChangeListener(com.sun.star.beans.XPropertySetInfoChangeListener Listener) {        
        throw new UnsupportedOperationException();
    }

    // com.sun.star.beans.XPropertiesChangeNotifier:
    public void addPropertiesChangeListener(String[] PropertyNames, com.sun.star.beans.XPropertiesChangeListener Listener) {
        propertiesChangeListeners.put(Listener, Arrays.asList(PropertyNames));
    }

    public void removePropertiesChangeListener(String[] PropertyNames, com.sun.star.beans.XPropertiesChangeListener Listener) {
        propertiesChangeListeners.remove(Listener);
    }

    // com.sun.star.container.XChild:
    public Object getParent() {        
        return null;            // Yet to Implement
    }

    public void setParent(Object Parent) throws com.sun.star.lang.NoSupportException {
        // TODO: Insert your implementation for "setParent" here.
    }

    // com.sun.star.ucb.XContent:
    public com.sun.star.ucb.XContentIdentifier getIdentifier() {
        return xContentid;
    }

    public String getContentType() {        
        return contentType;
    }

    private void contentListenerNotifier(ContentEvent event)
    {
        for(XContentEventListener eventListener:contentEventListeners)
            eventListener.contentEvent(event);
    }
    public void addContentEventListener(com.sun.star.ucb.XContentEventListener Listener) {
        contentEventListeners.add(Listener);
    }

    public void removeContentEventListener(com.sun.star.ucb.XContentEventListener Listener) {
        contentEventListeners.remove(Listener);
    }

    // com.sun.star.ucb.XCommandInfoChangeNotifier:
    public void addCommandInfoChangeListener(com.sun.star.ucb.XCommandInfoChangeListener Listener) {
        throw new UnsupportedOperationException();
    }

    public void removeCommandInfoChangeListener(com.sun.star.ucb.XCommandInfoChangeListener Listener) {
        throw new UnsupportedOperationException();
    }

    // com.sun.star.ucb.XCommandProcessor:
    public int createCommandIdentifier() {        
        return 0;        
    }
    
    public Object execute(com.sun.star.ucb.Command aCommand, int CommandId, com.sun.star.ucb.XCommandEnvironment Environment) throws com.sun.star.uno.Exception, com.sun.star.ucb.CommandAbortedException, NotConnectedException, IOException, InteractiveBadTransferURLException {
        XInteractionHandler xInteractionHandler;
        if(Environment!=null) 
            xInteractionHandler = Environment.getInteractionHandler();
        
        if (aCommand.Name.equalsIgnoreCase("getCommandInfo")) 
        {
            XCommandInfo xRet = new CMISCommandInfo(m_xContext);
            return xRet;
        }
        else if(aCommand.Name.equalsIgnoreCase("checkout"))
        {            
            return new Any(Type.BOOLEAN,resourceManager.getPrivateWorkingCopy());
        }
        else if(aCommand.Name.equalsIgnoreCase("checkin"))
        {
            CheckinCommandArgument checkinCommandArgument = (CheckinCommandArgument) AnyConverter.toObject(CheckinCommandArgument.class, aCommand.Argument);
            try 
            {
                return new Any(Type.BOOLEAN,resourceManager.checkIn(checkinCommandArgument.major, checkinCommandArgument.xInp, checkinCommandArgument.comment));
            }
            catch (java.io.IOException ex) 
            {
                Logger.getLogger(CMISContent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (aCommand.Name.equalsIgnoreCase("getPropertySetInfo")) 
        {
            XPropertySetInfo xRet = new CMISPropertySetInfo(m_xContext);
            return xRet;
        }
        else if (aCommand.Name.equalsIgnoreCase("getPropertyValues")) 
        {            
            Property[] rProperties;
            rProperties = (Property[]) AnyConverter.toArray(aCommand.Argument);                        
            return getPropertyValues(rProperties);
        }
        else if (aCommand.Name.equalsIgnoreCase("setPropertyValues")) 
        {
            PropertyValue[] pValues;
            try
            {
                pValues = (PropertyValue[]) AnyConverter.toArray(aCommand.Argument);
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("Incompatible argument");
            }                
            return setPropertyValues(pValues);
        }
        else if (aCommand.Name.equalsIgnoreCase("Open")) 
        {        
            OpenCommandArgument2 openArg;
            try
            {
                openArg = (OpenCommandArgument2) AnyConverter.toObject(OpenCommandArgument2.class, aCommand.Argument);
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("Unsupported argument");
            }
            
            if (isFolder()) 
            {
                if ((openArg.Mode == OpenMode.ALL) || (openArg.Mode == OpenMode.DOCUMENTS) || (openArg.Mode == OpenMode.FOLDERS)) 
                {                    
                    return openFolder(openArg);                
                }
                else
                {
                    throw new IllegalArgumentException("Document Modes not supported");
                }
            }
            else
            {
                if(openArg.Mode == OpenMode.DOCUMENT)
                {        
                    boolean pwc = false;
                    if(resourceManager.versionable && resourceManager.canCheckOut())
                    {
                        pwc = showCheckoutDialog();                    
                    }
                    XInputStream testStream = null;
                    try 
                    {
                        if(pwc==false)
                            testStream = resourceManager.getInputStream();
                        else
                        {                            
                            if(resourceManager.getPrivateWorkingCopy())
                            {
                                testStream = resourceManager.getInputStream();
                            }
                            else
                            {
                                testStream = resourceManager.getInputStream();
                            }
                        }
                    }
                    catch (java.io.IOException ex) 
                    {
                        Logger.getLogger(CMISContent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    final XInputStream xInp = testStream;
                    XActiveDataSink xDataSink = UnoRuntime.queryInterface(XActiveDataSink.class,openArg.Sink);
                    if(xDataSink!=null)
                        xDataSink.setInputStream(testStream);
                    else
                    {
                        XActiveDataStreamer xDataStream = UnoRuntime.queryInterface(XActiveDataStreamer.class, openArg.Sink);                                
                        if(xDataStream!=null)
                        {                                                                                     
                            XStream xStream = new XStream() {                                
                                public XInputStream getInputStream() {
                                    return xInp;
                                }

                                public XOutputStream getOutputStream() {
                                    return null;
                                }
                            };                            
                            xDataStream.setStream(xStream);
                        }
                        else
                        {
                            XOutputStream xOp = UnoRuntime.queryInterface(XOutputStream.class, openArg.Sink);
                            if(xOp!=null)
                            {
                                copyStream(xOp,xInp);
                            }
                            else
                            {
                                throw new IllegalArgumentException("Not XOutputStream/XActiveDataSink/XActiveDataStreamer");
                            }
                        }
                    }                    
                }
                else if(openArg.Mode == OpenMode.DOCUMENT_SHARE_DENY_NONE || openArg.Mode == OpenMode.DOCUMENT_SHARE_DENY_WRITE)
                    throw new UnsupportedCommandException();
                else
                    throw new IllegalArgumentException("Modes not supported for this content");
                                
            }        
        }
        else if(aCommand.Name.equalsIgnoreCase("CreatableContentsInfo"))
        {            
            return queryCreatableContentsInfo();
        }
        else if(aCommand.Name.equalsIgnoreCase("CreateNewContent"))
        {
            ContentInfo info;
            try
            {
                info = (ContentInfo) AnyConverter.toObject(ContentInfo.class, aCommand.Argument);
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("Incompatible argument");
            }
            return createNewContent(info);
        }
        else if(aCommand.Name.equalsIgnoreCase("Insert"))
        {
            InsertCommandArgument insertArg;
            try
            {
                insertArg = (InsertCommandArgument) AnyConverter.toObject(InsertCommandArgument.class, aCommand.Argument);
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("incompatible argument");
            }                
            
            try 
            {
                return insert(insertArg);
            }
            catch (java.io.IOException ex) 
            {
                throw new IllegalArgumentException("IO stream failure. Illegal datasink");
            }
        }
        else if(aCommand.Name.equalsIgnoreCase("transfer"))
        {
            TransferInfo aTransInfo;
            try
            {
                aTransInfo = (TransferInfo) AnyConverter.toObject(TransferInfo.class, aCommand.Argument);                            
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("Incompatible Transfer Argumnet");
            }            
            log.fine("just like that");
            try {
                transferDocument(aTransInfo, Environment);
                /*
                CMISConnect transferConnect;
                try
                {
                    transferConnect = new CMISConnect(m_xContext,aTransInfo.SourceURL, "rajaths589", "*****");
                }
                catch(CmisBaseException e)
                {
                    throw new IllegalArgumentException("Wrong repository data. Probably username,password/soruceURL is wrong");
                }
 
                //Name Clash not implemented.
                try 
                {
                    if(aTransInfo.NewTitle!=null)
                        resourceManager.transfer(transferConnect, aTransInfo.NewTitle);
                    else
                        resourceManager.transfer(transferConnect,transferConnect.getObject().getName());
                }    
                catch (java.io.IOException ex) 
                {
                    throw new IllegalArgumentException();
                }
                if(aTransInfo.MoveData==true)
                {                
                    CMISResourceManager transferRes = new CMISResourceManager(m_xContext, transferConnect.getObject(), transferConnect.getSession());
                    transferRes.delete();
                }*/
                /*
                if(aTransInfo.SourceURL.startsWith("cmis")||aTransInfo.SourceURL.startsWith("cmiss"))
                {
                    CMISConnect transferConnect;
                    try
                    {
                        transferConnect = new CMISConnect(m_xContext,aTransInfo.SourceURL, "rajaths589", "*****");
                    }
                    catch(CmisBaseException e)
                    {
                        throw new IllegalArgumentException("Wrong repository data. Probably username,password/soruceURL is wrong");
                    }
     
                    //Name Clash not implemented.
                    try 
                    {
                        if(aTransInfo.NewTitle!=null)
                            resourceManager.transfer(transferConnect, aTransInfo.NewTitle);
                        else
                            resourceManager.transfer(transferConnect,transferConnect.getObject().getName());
                    }    
                    catch (java.io.IOException ex) 
                    {
                        throw new IllegalArgumentException();
                    }
                    if(aTransInfo.MoveData==true)
                    {                
                        CMISResourceManager transferRes = new CMISResourceManager(m_xContext, transferConnect);
                        transferRes.delete();
                    }
                }
                else
                {
                    XContentIdentifier xTempTransfer;
                    if(ucbInitialized)
                        xTempTransfer = xContentIdentifierFactory.createContentIdentifier(aTransInfo.SourceURL);
                    else
                    {
                        initializeUCB();
                        xTempTransfer = xContentIdentifierFactory.createContentIdentifier(aTransInfo.SourceURL);
                    }
                    
                    XContent transferContent = xContentProvider.queryContent(xTempTransfer);
                    if(transferContent!=null)
                    {
                        XCommandProcessor xCP = UnoRuntime.queryInterface(XCommandProcessor.class, transferContent);
                        Command isTransFolder = new Command();
                        isTransFolder.Name = "getPropertyValues";
                        isTransFolder.Handle = -1;
                        
                        Property parr[] = new Property[1];
                        Property pFolder = new Property();
                        pFolder.Name = "isFolder";
                        pFolder.Handle = -1;
                        pFolder.Type = Type.BOOLEAN;
                        pFolder.Attributes = PropertyAttribute.READONLY;
                        parr[0] = pFolder;
                        isTransFolder.Argument = parr;
                                            
                        if(!(((XRow)AnyConverter.toObject(XRow.class,xCP.execute(isTransFolder, -1, Environment))).getBoolean(1)))
                        {
                            if(aTransInfo.NewTitle!=null) 
                            {
                                XActiveDataSink xActiveDataSink = new CMISActiveDataSink(m_xContext);
                                Command openDoc = new Command();
                                openDoc.Name = "open";
                                openDoc.Handle = -1;
                                OpenCommandArgument2 tempOpen = new OpenCommandArgument2();
                                tempOpen.Mode = OpenMode.DOCUMENT;
                                tempOpen.Sink = new Any(new Type(XActiveDataSink.class),xActiveDataSink);
                                openDoc.Argument = tempOpen;
                                xCP.execute(openDoc, -1, Environment);
                                String mimetype;
                                Command mimeType = new Command();
                                mimeType.Name = "getPropertyValues";
                                Property p[] = new Property[1];
                                Property pMime = new Property();
                                pMime.Name = "MediaType";
                                pMime.Handle = -1;
                                pMime.Attributes = PropertyAttribute.READONLY;
                                pMime.Type = Type.STRING;
                                p[0] = pMime;
                                mimeType.Handle = -1;
                                mimeType.Argument = p;
                                mimetype = UnoRuntime.queryInterface(XRow.class, xCP.execute(mimeType, -1, Environment)).getString(1);
                                try 
                                {
                                    resourceManager.createDocument(xActiveDataSink.getInputStream(), aTransInfo.NewTitle, mimetype);
                                }
                                catch (java.io.IOException ex) 
                                {
                                    Logger.getLogger(CMISContent.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if(aTransInfo.MoveData)
                                {
                                    Command del = new Command();
                                    del.Name = "delete";
                                    del.Handle = -1;
                                    xCP.execute(del, -1, null);
                                }
                            }
                            else 
                            {
                                // Move folder
                                
                            }
                        }
                    }
                }*/
            } catch (java.io.IOException ex) {
                Logger.getLogger(CMISContent.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        else if(aCommand.Name.equalsIgnoreCase("delete"))
        {
            ContentEvent arg = new ContentEvent();
            arg.Action = ContentAction.DELETED;
            arg.Content = this;
            arg.Id = xContentid;            
            
            resourceManager.delete();
            ContentEvent arg1 = new ContentEvent();
            arg1.Action = ContentAction.REMOVED;
            arg1.Content = this;
            arg1.Id = xContentid; 
            if(aConnect!=null)
            {    
                if(getIdentifier().getContentProviderScheme().equals("cmis"))
                {                
                    XContentIdentifier xIdParent = new CMISContentIdentifier(m_xContext, aConnect.getParentURL());
                    CMISContent xParent = new CMISContent(m_xContext, xIdParent);
                    xParent.contentListenerNotifier(arg1);
                }
                else if(getIdentifier().getContentProviderScheme().equals("cmiss"))
                {
                    XContentIdentifier xIdParent = new CMISSContentIdentifier(m_xContext, aConnect.getParentURL());
                    CMISContent xParent = new CMISContent(m_xContext, xIdParent);
                    xParent.contentListenerNotifier(arg1);
                }
            }
            contentListenerNotifier(arg);
        }        
        return com.sun.star.uno.Any.VOID;
    }
    private void loadDocumentReadOnly(CMISResourceManager manager) throws com.sun.star.uno.Exception
    {
        XMultiComponentFactory xMCF = m_xContext.getServiceManager();
        Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);
        XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
        XFrame current_frame = xDesktop.getCurrentFrame();
        XComponentLoader xComponentLoader = (XComponentLoader)UnoRuntime.queryInterface(XComponentLoader.class, desktop);                
        try
        {
            PropertyValue pv[] = new PropertyValue[1];
            pv[0] = new PropertyValue();
            pv[0].Name = "ReadOnly";
            pv[0].Value = new Any(Type.BOOLEAN,true);
            pv[0].Handle = -1;
            xComponentLoader.loadComponentFromURL(manager.getCompleteURL(),current_frame.getName(), FrameSearchFlag.ALL, pv);
        }
        catch(Exception e)
        {
            log.info("what the hell is this");
        }
    }
    
    private boolean transferDocument(TransferInfo trans, XCommandEnvironment xCmdEnv) throws com.sun.star.uno.Exception, java.io.IOException
    {        
        initializeUCB();
        if(resourceManager.isFolder)
        {
            //String sourceURL = trans.SourceURL;
            XContentIdentifier sourceIdentifier = xContentIdentifierFactory.createContentIdentifier(trans.SourceURL);
            XContent transContent = xContentProvider.queryContent(sourceIdentifier);
            XCommandProcessor  xCmdProcessor = UnoRuntime.queryInterface(XCommandProcessor.class, transContent);
            Command title = new Command();
            title.Name = "getPropertyValues";
            Property nameProp[] = new Property[2];
            nameProp[0] = new Property();
            nameProp[0].Name = "Title";
            nameProp[0].Handle = -1;
            nameProp[1] = new Property();
            nameProp[1].Name = "MediaType";
            nameProp[1].Handle = -1;
            title.Argument = nameProp;
            XRow name = UnoRuntime.queryInterface(XRow.class,xCmdProcessor.execute(title, -1, xCmdEnv));
            Command open = new Command();
            open.Name = "open";
            open.Handle = -1;
            OpenCommandArgument2 openArg = new OpenCommandArgument2();
            openArg.Mode = OpenMode.DOCUMENT;
            XActiveDataSink dataSink = new CMISActiveDataSink(m_xContext);
            openArg.Sink = dataSink;
            open.Argument = openArg;
            xCmdProcessor.execute(open, -1, xCmdEnv);            
            CMISResourceManager childTempManager = resourceManager.getChild(trans.NewTitle);            
            if(childTempManager!=null)
            {
                if(childTempManager.versionable)
                {                    
                    if(childTempManager.isCheckedOut())
                    {                                            
                        XPackageInformationProvider infoProvider = PackageInformationProvider.get(m_xContext);                             
                        String xdlLoc = infoProvider.getPackageLocation("org.apache.aoo.cmisucp.CMISContentProvider")+"/dialogs/CheckinDialog.xdl";
                        XMultiComponentFactory xMCF = m_xContext.getServiceManager();
                        Object dialogProvider = xMCF.createInstanceWithContext("com.sun.star.awt.DialogProvider2", m_xContext);
                        XDialogProvider2 xDialogProvider2 = UnoRuntime.queryInterface(XDialogProvider2.class, dialogProvider);
                        Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);
                        XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
                        XFrame current_frame = xDesktop.getCurrentFrame();
                        XDialog checkinDialog = xDialogProvider2.createDialog(xdlLoc);
                        XControlContainer xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, checkinDialog);
                        XTextComponent checkinComment = UnoRuntime.queryInterface(XTextComponent.class, xControlContainer.getControl("TextField1"));
                        XButton checkinBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton1"));
                        XButton cancelCheckout = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton2"));
                        XButton cancelBtn = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton3"));                
                        XRadioButton isMajorBtn = UnoRuntime.queryInterface(XRadioButton.class, xControlContainer.getControl("OptionButton1"));
                        checkinBtn.setActionCommand("checkin");
                        cancelCheckout.setActionCommand("cancel checkout");
                        cancelBtn.setActionCommand("cancel");
                        BtnActionListener checkinListenr = new BtnActionListener(checkinDialog);
                        checkinBtn.addActionListener(checkinListenr);
                        cancelCheckout.addActionListener(checkinListenr);
                        cancelBtn.addActionListener(checkinListenr);
                        checkinDialog.execute();
                        if(checkinListenr.getAccepted())
                        {
                            boolean isMajor = isMajorBtn.getState();
                            String comment = checkinComment.getText();
                            childTempManager.checkIn(isMajor, dataSink.getInputStream(), comment);
                            loadDocumentReadOnly(childTempManager);
                            return true;
                        }
                        if(checkinListenr.getCancelCheckout())
                        {
                            childTempManager.cancelCheckOut();
                            // more - code to do
                            return true;
                        }
                    }
                    else
                    {
                        if(childTempManager.canSetContentStream())
                        {
                            childTempManager.setInputStream(dataSink.getInputStream());
                            return true;
                        }
                    }
                }
                else
                {
                    if(childTempManager.canSetContentStream())
                    {
                        childTempManager.setInputStream(dataSink.getInputStream());
                        return true;
                    }
                    else
                    {
                        throw new CommandFailedException("Read-only object");
                    }
                }
            }
        }
        return false;
    }
    
    private boolean showCheckoutDialog() throws com.sun.star.uno.Exception
    {
        XMultiComponentFactory xMCF = m_xContext.getServiceManager();
        Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);
        XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
        XFrame current_frame = xDesktop.getCurrentFrame();
        Object dialogProvider = xMCF.createInstanceWithContext("com.sun.star.awt.DialogProvider2", m_xContext);
        XDialogProvider2 xDialogProvider2 = UnoRuntime.queryInterface(XDialogProvider2.class, dialogProvider);
        XPackageInformationProvider packageInfo = PackageInformationProvider.get(m_xContext);
        String location = packageInfo.getPackageLocation("org.apache.aoo.cmisucp.CMISContentProvider");
        String dialogURL = location+"/dialogs/CheckoutDialog.xdl";        
        XDialog xDialog = xDialogProvider2.createDialog(dialogURL);        
        XControlContainer xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, xDialog);
        XButton yesButton = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton1"));
        XButton cancelButton = UnoRuntime.queryInterface(XButton.class, xControlContainer.getControl("CommandButton2"));
        BtnActionListener btnActn = new BtnActionListener(xDialog);
        yesButton.setActionCommand("yes");
        cancelButton.setActionCommand("cancel");
        yesButton.addActionListener(btnActn);
        cancelButton.addActionListener(btnActn);        
        xDialog.setTitle("Checkout");                
        xDialog.execute();
        boolean checkout = btnActn.getAccepted();        
        return checkout;
    }
    private void setupDialogControls()
    {
        
    }
    private void initializeUCB() throws com.sun.star.uno.Exception
    {
        xmcf = m_xContext.getServiceManager();
        String keys[] = new String[2];
        keys[0] = "Local";
        keys[1] = "Office";
                
        xUCB = (XInterface) xmcf.createInstanceWithArgumentsAndContext("com.sun.star.ucb.UniversalContentBroker", keys,m_xContext);
        xContentIdentifierFactory = (XContentIdentifierFactory)UnoRuntime.queryInterface(XContentIdentifierFactory.class, xUCB);
        xContentProvider = (XContentProvider)UnoRuntime.queryInterface(XContentProvider.class, xUCB);
        
        ucbInitialized = true;
    }
        
    private void transferDifferentSource(XContent transContent, String Name, XCommandEnvironment xCommandEnv, CMISResourceManager resourceManager1) throws com.sun.star.uno.Exception, java.io.IOException
    {
        XCommandProcessor xCommandProcessor = UnoRuntime.queryInterface(XCommandProcessor.class, transContent);
        resourceManager1.createFolder(Name);
        CMISResourceManager newFolderManager = resourceManager1.getChild(Name);
        Property prop[] = new Property[2];
        prop[0] = new Property("Title",-1, Type.STRING, PropertyAttribute.BOUND);
        prop[1] = new Property("MediaType", -1, Type.STRING, PropertyAttribute.READONLY);
        OpenCommandArgument2 openFolderChildren = new OpenCommandArgument2();
        openFolderChildren.Properties = prop;
        openFolderChildren.Mode = OpenMode.DOCUMENTS;
        Command cmd = new Command();
        cmd.Name = "open";
        cmd.Argument = openFolderChildren;
        cmd.Handle = -1;
        
        XDynamicResultSet xDynamicResultSet = UnoRuntime.queryInterface(XDynamicResultSet.class, xCommandProcessor.execute(cmd, -1, xCommandEnv));
        XResultSet xResultSet = xDynamicResultSet.getStaticResultSet();
        
        while(xResultSet.next())
        {
            XRow rowProps = UnoRuntime.queryInterface(XRow.class, xResultSet);
            String title = rowProps.getString(1);
            String mimeType = rowProps.getString(2);
            XActiveDataSink xActiveDataSink = new CMISActiveDataSink(m_xContext);
            Command openDoc = new Command();
            openDoc.Name = "open";
            openDoc.Handle = -1;
            OpenCommandArgument2 tempOpen = new OpenCommandArgument2();
            tempOpen.Mode = OpenMode.DOCUMENT;
            tempOpen.Sink = new Any(new Type(XActiveDataSink.class),xActiveDataSink);
            openDoc.Argument = tempOpen;
            String child_identifier = transContent.getIdentifier().getContentIdentifier()+"/"+title;
            XContentIdentifier xChildID = xContentIdentifierFactory.createContentIdentifier(child_identifier);
            XContent xChildDoc = xContentProvider.queryContent(xChildID);
            XCommandProcessor xCmdChild = (XCommandProcessor) UnoRuntime.queryInterface(XCommandProcessor.class, xChildDoc);
            xCmdChild.execute(openDoc, -1, xCommandEnv);                        
            newFolderManager.createDocument(xActiveDataSink.getInputStream(), title, mimeType);
        }
        openFolderChildren.Mode = OpenMode.FOLDERS;
        Property props[] = new Property[1];
        props[0] = new Property("Title", -1, Type.STRING, PropertyAttribute.BOUND);
        xDynamicResultSet = UnoRuntime.queryInterface(XDynamicResultSet.class, xCommandProcessor.execute(cmd, -1, xCommandEnv));
        xResultSet = xDynamicResultSet.getStaticResultSet();
        while(xResultSet.next())
        {
            XRow rowProps = UnoRuntime.queryInterface(XRow.class, xResultSet);
            String title = rowProps.getString(1);
            String child_identifier = transContent.getIdentifier().getContentIdentifier()+"/"+title;
            XContentIdentifier xChildID = xContentIdentifierFactory.createContentIdentifier(child_identifier);
            XContent xChildFolder = xContentProvider.queryContent(xChildID);
            transferDifferentSource(xChildFolder, title, xCommandEnv, newFolderManager);
        }
    }
    
    //My method
    private void copyStream(XOutputStream xOut, XInputStream xInp) throws NotConnectedException, BufferSizeExceededException, IOException
    {
        byte[][] buffer = new byte[1][1];
        int len = xInp.readBytes(buffer, 1024);
        while(len==1024)
        {
            xOut.writeBytes(buffer[0]);
            len = xInp.readBytes(buffer, 1024);
        }
    }
    
    //My method
    private Object insert(InsertCommandArgument iArg) throws NotConnectedException, IOException, java.io.IOException, ContentCreationException
    {
        if(exists)
        {
            if(!is_folder)
            {
                // To-be tested
                ContentEvent arg = new ContentEvent();
                arg.Content = this;
                arg.Id = xContentid;
                arg.Action = ContentAction.INSERTED;                
                resourceManager.setInputStream(iArg.Data);
                contentListenerNotifier(arg);
                return null;
            }
            else
            {
                Folder parentFolder = resourceManager.getParent();
                FileUtils.delete(resourceManager.getID(),connected_session);
                ContentEvent arg = new ContentEvent();
                arg.Action = ContentAction.INSERTED;
                arg.Content = this;
                arg.Id = xContentid;
                cmisContent  = FileUtils.createFolder(parentFolder, path.substring(6,path.lastIndexOf("/")+1), "cmis:folder");
                //resourceManager = new CMISResourceManager(m_xContext, cmisContent, connected_session, );               
                if(getIdentifier().getContentProviderScheme().equals("cmis"))
                {
                    XContentIdentifier xIdParent = new CMISContentIdentifier(m_xContext, getIdentifier().getContentIdentifier());
                    CMISContent xParent = new CMISContent(m_xContext, xIdParent);
                    xParent.contentListenerNotifier(arg);
                }
                else if(getIdentifier().getContentProviderScheme().equals("cmiss"))
                {
                    XContentIdentifier xIdParent = new CMISSContentIdentifier(m_xContext, getIdentifier().getContentIdentifier());
                    CMISContent xParent = new CMISContent(m_xContext, xIdParent);
                    xParent.contentListenerNotifier(arg);
                }                
                // Content Event to be added.
                return null;
            }
        }
        else
        {
            if(is_folder)
            {
               if(nameSet) 
               {
                   String parentUri = xContentid.getContentIdentifier().substring(0, xContentid.getContentIdentifier().lastIndexOf('/'));
                   processIdentifier(parentUri);
                   if(resourceManager.createFolder(xContentid.getContentIdentifier().substring(xContentid.getContentIdentifier().lastIndexOf("/")+1)))
                   {
                        ContentEvent arg = new ContentEvent();
                        arg.Action = ContentAction.INSERTED;
                        arg.Content = this;
                        arg.Id = xContentid;
                        exists = true;                        
                        processIdentifier(xContentid.getContentIdentifier());
                        contentListenerNotifier(arg);
                        //ContentEvent to be created
                   }
               }
               else
               {                
                   throw new UnsupportedOperationException("Name not set");                   
               }
            }
            else
            {
                if(nameSet)
                {
                    String parentUri = xContentid.getContentIdentifier().substring(0, xContentid.getContentIdentifier().lastIndexOf('/'));
                    processIdentifier(parentUri);
                    if(resourceManager.createDocument(iArg.Data,xContentid.getContentIdentifier().substring(xContentid.getContentIdentifier().lastIndexOf("/")+1), "txt/plain"))
                    {
                        exists = true;
                        ContentEvent arg = new ContentEvent();
                        arg.Action = ContentAction.INSERTED;
                        arg.Content = this;
                        arg.Id = xContentid;
                        processIdentifier(xContentid.getContentIdentifier());
                        contentListenerNotifier(arg);
                    }
                }
                else
                {
                    //throw property values not set exception.
                    throw new UnsupportedOperationException("Name not set");
                }
            }
        }
        return null;
    }
    //My method
    private Any openFolder(OpenCommandArgument2 oarg) {
        List<PropertyAndValueSet> open_result = new ArrayList<PropertyAndValueSet>();       
        if(!exists)
            return null;
        
        Folder f = resourceManager.getFolder();
        OperationContext opCon = connected_session.createOperationContext();
        
        if (oarg.Mode == OpenMode.ALL) 
        {
            for (CmisObject o : f.getChildren()) 
            {                           
                CMISResourceManager tempChildResource;
                if(xContentid.getContentIdentifier().endsWith("/"))
                    tempChildResource = new CMISResourceManager(m_xContext, o, connected_session, xContentid.getContentIdentifier()+o.getName());
                else
                    tempChildResource = new CMISResourceManager(m_xContext, o, connected_session, xContentid.getContentIdentifier()+"/"+o.getName());
                
                Any values[] = tempChildResource.getPropertiesAsAny(oarg.Properties);
                PropertyAndValueSet childValue;
                if(xContentid.getContentIdentifier().endsWith("/"))                    
                    childValue = new PropertyAndValueSet(xContentid.getContentIdentifier()+tempChildResource.getName());
                else
                    childValue = new PropertyAndValueSet(xContentid.getContentIdentifier()+"/"+tempChildResource.getName());
                childValue.updateProperties(oarg.Properties, values);
                open_result.add(childValue);
            }
        } 
        else if (oarg.Mode == OpenMode.DOCUMENTS) 
        {
            opCon.setFilterString("cmis:document");
            for (CmisObject o : f.getChildren(opCon)) 
            {                
                CMISResourceManager tempChildResource;
                if(xContentid.getContentIdentifier().endsWith("/"))
                    tempChildResource = new CMISResourceManager(m_xContext, o, connected_session, xContentid.getContentIdentifier()+o.getName());
                else
                    tempChildResource = new CMISResourceManager(m_xContext, o, connected_session, xContentid.getContentIdentifier()+"/"+o.getName());
                
                
                Any values[] = tempChildResource.getPropertiesAsAny(oarg.Properties);
                PropertyAndValueSet childValue;
                if(xContentid.getContentIdentifier().endsWith("/"))                    
                    childValue = new PropertyAndValueSet(xContentid.getContentIdentifier()+tempChildResource.getName());
                else
                    childValue = new PropertyAndValueSet(xContentid.getContentIdentifier()+"/"+tempChildResource.getName());
                childValue.updateProperties(oarg.Properties, values);
                open_result.add(childValue);
            }
        } 
        else 
        {
            opCon.setFilterString("cmis:folder");
            for (CmisObject o : f.getChildren(opCon)) 
            {
                CMISResourceManager tempChildResource;
                if(xContentid.getContentIdentifier().endsWith("/"))
                    tempChildResource = new CMISResourceManager(m_xContext, o, connected_session, xContentid.getContentIdentifier()+o.getName());
                else
                    tempChildResource = new CMISResourceManager(m_xContext, o, connected_session, xContentid.getContentIdentifier()+"/"+o.getName());
                
                
                Any values[] = tempChildResource.getPropertiesAsAny(oarg.Properties);
                PropertyAndValueSet childValue;
                if(xContentid.getContentIdentifier().endsWith("/"))                    
                    childValue = new PropertyAndValueSet(xContentid.getContentIdentifier()+tempChildResource.getName());
                else
                    childValue = new PropertyAndValueSet(xContentid.getContentIdentifier()+"/"+tempChildResource.getName());
                childValue.updateProperties(oarg.Properties, values);
                childValue.updateProperties(oarg.Properties, values);
                open_result.add(childValue);
        }
    }
    ContentPropertySet cpSet = new ContentPropertySet(m_xContext, open_result.toArray(new PropertyAndValueSet[0]));
    return new Any(new Type(ContentPropertySet.class),cpSet);
}
//My method
private Any getPropertyValues(Property[] request)
    {
        if(!exists)
            return null;
        //switching to PropertyAndValueSet
        
        if(request == null)
        {
            CMISPropertySetInfo propSetInfo = new CMISPropertySetInfo(m_xContext);
            request = propSetInfo.getProperties();
        }        
        
        Any value[] = resourceManager.getPropertiesAsAny(request);                                        
        PropertyAndValueSet propertiesSet = new PropertyAndValueSet(xContentid.getContentIdentifier());
        propertiesSet.updateProperties(request, value);
        return new Any(new Type(XRow.class),propertiesSet);
    }            
    
    //My method
    private Any[] setPropertyValues(PropertyValue[] pValues) throws UnknownPropertyException, IllegalArgumentException
    {
        Any ans[] = new Any[pValues.length];
        CMISPropertySetInfo xCommandInfo = new CMISPropertySetInfo(m_xContext);
        int index = 0;
        ContentEvent conEV = new ContentEvent();
        conEV.Action = ContentAction.EXCHANGED;
        conEV.Content = this;
        conEV.Id = getIdentifier();
        
        for(PropertyValue p:pValues)
        {
            if(CMISConstants.propertiesHashMap.containsKey(p.Name))
            {
                if(xCommandInfo.getPropertyByName(p.Name).Attributes == PropertyAttribute.READONLY)
                    ans[index] = (Any) AnyConverter.toObject(IllegalAccessException.class ,new IllegalAccessException());
                //To - DO
                else if(p.Name.equalsIgnoreCase("Title"))
                {
                    if(exists)
                    {
                        String title;
                        try{
                            title = AnyConverter.toString(p.Value);
                        }catch(Exception e){
                            throw new IllegalArgumentException("Incompatible argument");
                        }                            
                                                                
                        try
                        {
                            PropertyChangeEvent event = new PropertyChangeEvent();
                            event.PropertyName = "Title";
                            event.PropertyHandle = -1;
                            event.OldValue = resourceManager.getName();
                            event.Further = false;
                            resourceManager.setName(title);
                            event.NewValue = title;
                            notifyPropertyListeners(event);
                            ans[index] = null;
                            contentListenerNotifier(conEV);
                        }
                        catch(CmisNameConstraintViolationException e)
                        {
                            ans[index] = new Any(Type.ANY,AnyConverter.toObject(IllegalArgumentException.class, new IllegalArgumentException("Name Constraint Violated")));
                        }
                        catch(CmisContentAlreadyExistsException e)
                        {
                            ans[index] = (Any) AnyConverter.toObject(IllegalArgumentException.class, new IllegalArgumentException("Content Already Exists"));
                        }
                    // other exceptions
                    }
                    else
                    {
                        String url;
                        if(xContentid==null)
                        {
                            try{
                                path = path+"/"+AnyConverter.toString(p.Value);
                            }catch(Exception e)
                            {
                                throw new IllegalArgumentException("Incompatible Argument");
                            }
                            PropertyChangeEvent event = new PropertyChangeEvent();
                            event.Further = false;
                            event.OldValue = null;
                            event.PropertyName = "Title";
                            event.PropertyHandle = -1;
                            xContentid = new CMISContentIdentifier(m_xContext, path);
                            event.NewValue = p.Value;
                            ans[index] = null;
                            nameSet = true;
                            notifyPropertyListeners(event);
                            contentListenerNotifier(conEV);
                        }
                    }
                }   
                    
            }
            
            index ++;
        }
        return ans;
    }
    
    private void notifyPropertyListeners(PropertyChangeEvent e)
    {
        PropertyChangeEvent eventArray[] = new PropertyChangeEvent[1];
        eventArray[0] = e;
        for(XPropertiesChangeListener xP:propertiesChangeListeners.keySet())
        {
            for(String s:propertiesChangeListeners.get(xP))
            {
                if(s.equals(e.PropertyName))
                {
                    xP.propertiesChange(eventArray);
                }
            }
        }
    }
    public void abort(int CommandId)
    {
        // TODO: Insert your implementation for "abort" here.
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
         return m_implementationName;
    }

    public boolean supportsService( String sService ) {
        int len = m_serviceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    // com.sun.star.ucb.XCommandProcessor2:
    public void releaseCommandIdentifier(int CommandId)
    {
        // TODO: Insert your implementation for "releaseCommandIdentifier" here.
    }

    // com.sun.star.ucb.XContentCreator:
    public com.sun.star.ucb.ContentInfo[] queryCreatableContentsInfo()
    {
        ContentInfo info[] = new ContentInfo[2];
        
        Property p[] = new Property[1];
        p[0] = new Property("Title", -1, Type.STRING, PropertyAttribute.BOUND);
                
        info[0] = new ContentInfo("application/cmis-folder", ContentInfoAttribute.KIND_FOLDER, p);
        info[1] = new ContentInfo("application/cmis-document",ContentInfoAttribute.INSERT_WITH_INPUTSTREAM|ContentInfoAttribute.KIND_DOCUMENT,p);
                        
        
        if(isFolder())
        {
            ContentInfo[] returnInfo;
            List<Object> oT = cmisContent.getProperty(PropertyIds.ALLOWED_CHILD_OBJECT_TYPE_IDS).getValues();
            int flag = 0;
            
            for(Object o:oT)
            {
                if(o.toString().equals("*"))
                {
                    flag = 3;
                    break;
                }
                else if(o.toString().equalsIgnoreCase(ObjectType.DOCUMENT_BASETYPE_ID))
                {
                    flag += 1;
                }
                else if(o.toString().equalsIgnoreCase(ObjectType.FOLDER_BASETYPE_ID))
                {
                    flag += 2;
                }
                
            }
            if(flag==3)
                return info;        
            else if(flag==1)
            {
                returnInfo = new ContentInfo[1];
                returnInfo[0] = info[0];
                return returnInfo;
            }
            else if(flag==2)
            {
                returnInfo = new ContentInfo[1];
                returnInfo[0] = info[1];
                return returnInfo;
            }
            
        }
        else
            return null;
        // Deprecated;                                
        return null;
    }

    public com.sun.star.ucb.XContent createNewContent(com.sun.star.ucb.ContentInfo Info)
    {        
        XContent xContent = null;
        try {
            xContent = new CMISContent(m_xContext, Info.Type, path);
        } catch (ContentCreationException ex) {
            Logger.getLogger(CMISContent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotConnectedException ex) {
            Logger.getLogger(CMISContent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CMISContent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return xContent;
    }

    // com.sun.star.beans.XPropertyContainer:
    public void addProperty(String Name, short Attributes, Object DefaultValue) throws com.sun.star.beans.PropertyExistException, com.sun.star.beans.IllegalTypeException, com.sun.star.lang.IllegalArgumentException
    {
        // TODO: Insert your implementation for "addProperty" here.
    }

    public void removeProperty(String Name) throws com.sun.star.beans.UnknownPropertyException, com.sun.star.beans.NotRemoveableException
    {
        // TODO: Insert your implementation for "removeProperty" here.
    }
    
    public class BtnActionListener implements XActionListener
    {       
        private boolean accepted;       
        private XDialog m_xDialog;
        private boolean cancelCheckout = false;
        public BtnActionListener(XDialog xDialog)
        {
            m_xDialog = xDialog;            
        }
        public void actionPerformed(ActionEvent arg0) {
            if(arg0.ActionCommand.equalsIgnoreCase("yes"))
            {     
                accepted = true;
                m_xDialog.endExecute();
            }
            else if(arg0.ActionCommand.equalsIgnoreCase("cancel"))
            {             
                accepted = false;
                m_xDialog.endExecute();
            }
            else if(arg0.ActionCommand.equalsIgnoreCase("checkin"))
            {
                accepted = true;
                m_xDialog.endExecute();
            }
            else if(arg0.ActionCommand.equalsIgnoreCase("cancel checkout"))
            {
                accepted = false;
                cancelCheckout = true;
                m_xDialog.endExecute();
            }            
        }
        public boolean getAccepted()
        {
            return accepted;
        }
        public boolean getCancelCheckout()
        {
            return cancelCheckout;                   
        }
        public void disposing(EventObject arg0) {
            m_xDialog = null;
        }
        
    }
}
