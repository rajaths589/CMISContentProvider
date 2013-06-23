package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyAttribute;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.IOException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.io.XActiveDataSink;
import com.sun.star.io.XActiveDataStreamer;
import com.sun.star.io.XInputStream;
import com.sun.star.io.XOutputStream;
import com.sun.star.io.XStream;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.ComponentBase;
import com.sun.star.sdbc.SQLException;
import com.sun.star.sdbc.XRow;
import com.sun.star.ucb.ContentInfo;
import com.sun.star.ucb.ContentInfoAttribute;
import com.sun.star.ucb.InsertCommandArgument;
import com.sun.star.ucb.OpenCommandArgument2;
import com.sun.star.ucb.OpenMode;
import com.sun.star.ucb.UnsupportedCommandException;
import com.sun.star.ucb.XCommandInfo;
import com.sun.star.ucb.XContent;
import com.sun.star.ucb.XContentIdentifier;
import com.sun.star.ucb.XDynamicResultSet;
import com.sun.star.uno.Any;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.Type;
import com.sun.star.uno.UnoRuntime;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.aoo.cmisucp.CMISConstants;
import org.apache.aoo.cmisucp.cmis.CMISConnect;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.util.FileUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
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
    private boolean exists;
    private String path;
    
    public CMISContent(XComponentContext context, XContentIdentifier xContentIdentifier)  {
        m_xContext = context;

        xContentid = xContentIdentifier;
        //path = getRelativePath(xContentid.getContentIdentifier());

        processIdentifier(xContentIdentifier.getContentIdentifier());
        //exists = true;  
        path = xContentIdentifier.getContentIdentifier();
    }

    public CMISContent(XComponentContext context, String type, String uri)
    {
        m_xContext = context;
        contentType = type;
        if(type.equalsIgnoreCase("application/cmis-document"))
            is_folder = false;
        else
            is_folder = true;
        processIdentifier(uri);
        exists = false;        
        
        path = uri;
    }
    
    //My method
    public void processIdentifier(String uri) {

        uri = uri.substring(6);        
        
        log.log(Level.INFO, "URI:{0}", uri);

        CMISConnect aConnect;

        try
        {
            aConnect = new CMISConnect("http://localhost:8080/inmemory/atom", "rajaths589", "*****", "A1", uri);
            connected_session = aConnect.getSession();
            cmisContent = aConnect.getObject();
        }
        catch(CmisBaseException e)
        {
            exists = false;
            if(e.getExceptionName().equalsIgnoreCase(CmisConnectionException.EXCEPTION_NAME))
            {
                // To-Do . connected_session - not connected;
            }
            else if(e.getExceptionName().equalsIgnoreCase(CmisObjectNotFoundException.EXCEPTION_NAME))
            {
                
                    //throw unknown path exception                
                
            }
            else
            {
                // throw some expception
            }
            return;
        }
        exists = true;
        setContentType(aConnect);
        
        
    }

    private void setContentType(CMISConnect aConnect)
    {
        if(!exists)
            return;
        
        if (aConnect.getContentType().equalsIgnoreCase(BaseTypeId.CMIS_FOLDER.value())) {
            contentType = "application/cmis-folder";
            is_folder = true;
            
        } else {
            contentType = "application/cmis-document";
            is_folder = false;
        }

        
        id = cmisContent.getId();
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
        // TODO: Insert your implementation for "addPropertySetInfoChangeListener" here.
    }

    public void removePropertySetInfoChangeListener(com.sun.star.beans.XPropertySetInfoChangeListener Listener) {
        // TODO: Insert your implementation for "removePropertySetInfoChangeListener" here.
    }

    // com.sun.star.beans.XPropertiesChangeNotifier:
    public void addPropertiesChangeListener(String[] PropertyNames, com.sun.star.beans.XPropertiesChangeListener Listener) {
        // TODO: Insert your implementation for "addPropertiesChangeListener" here.
    }

    public void removePropertiesChangeListener(String[] PropertyNames, com.sun.star.beans.XPropertiesChangeListener Listener) {
        // TODO: Insert your implementation for "removePropertiesChangeListener" here.
    }

    // com.sun.star.container.XChild:
    public Object getParent() {
        //To-do
        return null;
    }

    public void setParent(Object Parent) throws com.sun.star.lang.NoSupportException {
        // TODO: Insert your implementation for "setParent" here.
    }

    // com.sun.star.ucb.XContent:
    public com.sun.star.ucb.XContentIdentifier getIdentifier() {
        return xContentid;
    }

    public String getContentType() {
        //To-Do
        return contentType;
    }

    public void addContentEventListener(com.sun.star.ucb.XContentEventListener Listener) {
        // TODO: Insert your implementation for "addContentEventListener" here.
    }

    public void removeContentEventListener(com.sun.star.ucb.XContentEventListener Listener) {
        // TODO: Insert your implementation for "removeContentEventListener" here.
    }

    // com.sun.star.ucb.XCommandInfoChangeNotifier:
    public void addCommandInfoChangeListener(com.sun.star.ucb.XCommandInfoChangeListener Listener) {
        // TODO: Insert your implementation for "addCommandInfoChangeListener" here.
    }

    public void removeCommandInfoChangeListener(com.sun.star.ucb.XCommandInfoChangeListener Listener) {
        // TODO: Insert your implementation for "removeCommandInfoChangeListener" here.
    }

    // com.sun.star.ucb.XCommandProcessor:
    public int createCommandIdentifier() {
        // TODO: Exchange the default return implementation for "createCommandIdentifier" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return 0;
    }

    public Object execute(com.sun.star.ucb.Command aCommand, int CommandId, com.sun.star.ucb.XCommandEnvironment Environment) throws com.sun.star.uno.Exception, com.sun.star.ucb.CommandAbortedException {
        if (aCommand.Name.equalsIgnoreCase("getCommandInfo")) 
        {
            XCommandInfo xRet = new CMISCommandInfo(m_xContext);
            return xRet;
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
            log.info("getPropertyValues()");
            return getPropertyValues(rProperties);
        }
        else if (aCommand.Name.equalsIgnoreCase("setPropertyValues")) 
        {
            PropertyValue[] pValues;
            pValues = (PropertyValue[]) AnyConverter.toArray(aCommand.Argument);
            log.info("setPropertyValues()");
            return setPropertyValues(pValues);
        }
        else if (aCommand.Name.equalsIgnoreCase("open")) 
        {
            log.info("open()");

            OpenCommandArgument2 openArg;
            openArg = (OpenCommandArgument2) AnyConverter.toObject(OpenCommandArgument2.class, aCommand.Argument);

            if (isFolder()) {
                if ((openArg.Mode == OpenMode.ALL) || (openArg.Mode == OpenMode.DOCUMENTS) || (openArg.Mode == OpenMode.FOLDERS)) {
                    XDynamicResultSet xRet;
                    xRet = openFolder(openArg);
                    return xRet;
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
                    final XInputStream xInp = new CMISInputStream(m_xContext, (Document)cmisContent);
                    XActiveDataSink xDataSink = UnoRuntime.queryInterface(XActiveDataSink.class,openArg.Sink);
                    if(xDataSink!=null)
                        xDataSink.setInputStream(xInp);
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
                                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.                                    
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
                                throw new IllegalArgumentException("Not xoutputstream/xactivedatasink/xactivedatastreamer");
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
            ContentInfo info = (ContentInfo) AnyConverter.toObject(ContentInfo.class, aCommand.Argument);
            return createNewContent(info);
        }
        else if(aCommand.Name.equalsIgnoreCase("Insert"))
        {
            InsertCommandArgument insertArg = (InsertCommandArgument) AnyConverter.toObject(InsertCommandArgument.class, aCommand.Argument);
            return insert(insertArg);
        }

        return com.sun.star.uno.Any.VOID;
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
    private Object insert(InsertCommandArgument iArg) throws NotConnectedException, IOException
    {
        if(exists)
        {
            if(!is_folder)
            {
                // To-be tested
                Document docContent = (Document)cmisContent;
                ContentStream conStream;
                byte buffer[][] = new byte[1][1];
                int readSize  = iArg.Data.readBytes(buffer, 1024);
                ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                while(readSize==1024)
                {
                    byteOutput.write(buffer[0], 0, 1024);
                    readSize  = iArg.Data.readBytes(buffer, 1024);
                }
                byteOutput.write(buffer[0], 0, 1024);
                
                ByteArrayInputStream byteInp = new ByteArrayInputStream(byteOutput.toByteArray());
                
                conStream = connected_session.getObjectFactory().createContentStream(cmisContent.getName(),iArg.Data.available() , cmisContent.getPropertyValue("cmis:contentStreamLength").toString(),  byteInp);
                
                docContent.setContentStream(conStream,false);
                
                    
                return null;
            }
            else
            {
                Folder parentFolder = ((Folder)cmisContent).getFolderParent();
                FileUtils.delete(id, connected_session);
                
                cmisContent  = FileUtils.createFolder(parentFolder, path.substring(6,path.lastIndexOf("/")+1), "cmis:folder");
            
                // Content Event to be added.
                return null;
            }
        }
        else
        {
            if(is_folder)
            {
               if(xContentid!=null) 
               {
                   String parentUri = xContentid.getContentIdentifier().substring(0, xContentid.getContentIdentifier().lastIndexOf('/'));
                   processIdentifier(parentUri);
                   Folder parent = (Folder)cmisContent;
                   Map<String,String> folderProps = new HashMap<String, String>();
                   folderProps.put(PropertyIds.NAME, xContentid.getContentIdentifier().substring(xContentid.getContentIdentifier().lastIndexOf("/")+1));
                   folderProps.put(PropertyIds.OBJECT_TYPE_ID, ObjectType.FOLDER_BASETYPE_ID);  
                   parent.createFolder(folderProps);
                   exists = true;
                   processIdentifier(xContentid.getContentIdentifier());
               }
               else
               {
                //throw some exception;
                   ;
               }
            }
            else
            {
                if(xContentid!=null)
                {
                    String parentUri = xContentid.getContentIdentifier().substring(0, xContentid.getContentIdentifier().lastIndexOf('/'));
                    processIdentifier(parentUri);
                    Folder parent = (Folder)cmisContent;
                    Map<String,String> docProps = new HashMap<String, String>();
                    docProps.put(PropertyIds.NAME, xContentid.getContentIdentifier().substring(xContentid.getContentIdentifier().lastIndexOf("/")+1));
                    docProps.put(PropertyIds.OBJECT_TYPE_ID, ObjectType.FOLDER_BASETYPE_ID);                   
                    ContentStream conStream;
                    byte buffer[][] = new byte[1][1];
                    int readSize  = iArg.Data.readBytes(buffer, 1024);
                    ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
                    while(readSize==1024)
                    {
                        byteOutput.write(buffer[0], 0, 1024);
                        readSize  = iArg.Data.readBytes(buffer, 1024);
                    }
                    byteOutput.write(buffer[0], 0, 1024);
                    
                    ByteArrayInputStream byteInp = new ByteArrayInputStream(byteOutput.toByteArray());
                
                    conStream = connected_session.getObjectFactory().createContentStream(cmisContent.getName(),iArg.Data.available() , cmisContent.getPropertyValue("cmis:contentStreamLength").toString(),  byteInp);
                    parent.createDocument(docProps, conStream, VersioningState.NONE);
                    exists = true;
                    processIdentifier(xContentid.getContentIdentifier());
                }
                else
                {
                    //throw property values not set exception.
                }
            }
        }
        return null;
    }
    //My method
    private XDynamicResultSet openFolder(OpenCommandArgument2 oarg) {
        List<XRow> open_result = new ArrayList<XRow>();
        Folder f = (Folder) cmisContent;
        if(!exists)
            return null;
        
        OperationContext opCon = connected_session.createOperationContext();
        if (oarg.Mode == OpenMode.ALL) 
        {
            for (CmisObject o : f.getChildren()) 
            {
                List<String> childProps;
                childProps = new ArrayList<String>();

                childProps.add(o.getId());
                for (Property p : oarg.Properties) 
                {
                    if (o.getBaseTypeId() == BaseTypeId.CMIS_DOCUMENT) 
                    {
                        if(p.Name.equalsIgnoreCase("ContentType"))
                        {
                            childProps.add("application/cmis-document");
                        }
                        else if (CMISConstants.propertiesHashMap.containsKey(p.Name)) 
                        {
                            childProps.add(o.getPropertyValue(CMISConstants.propertiesHashMap.get(p.Name)).toString());
                        } 
                        else 
                        {
                            childProps.add(null);
                        }
                    } 
                    else 
                    {
                        if (p.Name.equalsIgnoreCase("MediaType")) 
                        {
                            childProps.add(null);
                        }
                        else if (p.Name.equalsIgnoreCase("ContentType")) 
                        {
                            childProps.add("application/cmis-folder");
                        }
                        else if (p.Name.equalsIgnoreCase("Size")) 
                        {
                            String queryString;
                            queryString = "SELECT " + CMISConstants.propertiesHashMap.get("Size") + " FROM cmis:document WHERE IN_TREE(\'" + id + "\')";
                            long size = 0;
                            for (QueryResult qR : connected_session.query(queryString, false)) 
                            {
                                size += Long.parseLong(qR.getPropertyValueById("cmis:contentStreamLength").toString());
                            }
                            childProps.add(String.valueOf(size));
                        } 
                        else 
                        {                            
                            if (CMISConstants.propertiesHashMap.containsKey(p.Name)) 
                            {
                                childProps.add(o.getPropertyValue(CMISConstants.propertiesHashMap.get(p.Name)).toString());
                            } 
                            else 
                            {
                                childProps.add(null);
                            }
                        }
                    }
                }
                XRow xRow = new CMISRow(childProps);
                open_result.add(xRow);
            }
        } 
        else if (oarg.Mode == OpenMode.DOCUMENTS) 
        {
            opCon.setFilterString("cmis:document");
            for (CmisObject o : f.getChildren(opCon)) 
            {
                List<String> childProps;
                childProps = new ArrayList<String>();

                childProps.add(o.getId());
                for (Property p : oarg.Properties) 
                {
                    if(p.Name.equalsIgnoreCase("ContentType"))
                    {
                        childProps.add("application/cmis-document");
                    }
                    else if (CMISConstants.propertiesHashMap.containsKey(p.Name)) 
                    {
                        childProps.add(o.getPropertyValue(CMISConstants.propertiesHashMap.get(p.Name)).toString());
                    } 
                    else 
                    {
                        childProps.add(null);
                    }
                }
                XRow xRow = new CMISRow(childProps);
                open_result.add(xRow);
            }
        } 
        else 
        {
            opCon.setFilterString("cmis:folder");
            for (CmisObject o : f.getChildren(opCon)) 
            {
                List<String> childProps;
                childProps = new ArrayList<String>();

                childProps.add(o.getId());
                for (Property p : oarg.Properties) 
                {
                    if (p.Name.equalsIgnoreCase("MediaType")) 
                    {
                        childProps.add(null);
                    }
                    else if(p.Name.equalsIgnoreCase("ContentType"))
                    {
                        childProps.add("application/cmis-folder");
                    }
                    else if (p.Name.equalsIgnoreCase("Size")) 
                    {
                        String queryString;
                        queryString = "SELECT " + CMISConstants.propertiesHashMap.get("Size") + " FROM cmis:document WHERE IN_TREE(\'" + id + "\')";
                        long size = 0;
                        for (QueryResult qR : connected_session.query(queryString, false)) 
                        {
                            size += Long.parseLong(qR.getPropertyValueById("cmis:contentStreamLength").toString());
                        }
                        childProps.add(String.valueOf(size));
                    } 
                    else 
                    {
                        if (CMISConstants.propertiesHashMap.containsKey(p.Name)) 
                        {
                            childProps.add(o.getPropertyValue(CMISConstants.propertiesHashMap.get(p.Name)).toString());
                        }
                        else 
                        {
                            childProps.add(null);
                        }
                    }
                }
            
            XRow xRow = new CMISRow(childProps);
            open_result.add(xRow);
        }
    }
    XDynamicResultSet xDynamicResultSet = new CMISDynamicResultSet(m_xContext,open_result,oarg.Properties,xContentid);
    return xDynamicResultSet;
}
//My method
private XRow getPropertyValues(Property[] request)
    {
        if(!exists)
            return null;
        
        List<String> answers = new ArrayList<String>();
        
        answers.add(cmisContent.getId());
        if(request == null)
        {
            CMISPropertySetInfo propSetInfo = new CMISPropertySetInfo(m_xContext);
            request = propSetInfo.getProperties();
        }        
        if(isFolder())
        {
            for(Property p:request)
            {
                if (p.Name.equalsIgnoreCase("MediaType")) 
                {
                    answers.add(null);                
                }
                else if(p.Name.equalsIgnoreCase("ContentType"))
                    answers.add("application/cmis-folder");
                else if(p.Name.equalsIgnoreCase("Size"))
                {
                    String queryString;
                    queryString = "SELECT " + CMISConstants.propertiesHashMap.get("Size") + " FROM cmis:document WHERE IN_TREE(\'" + id + "\')";
                    long size = 0;
                    for (QueryResult qR : connected_session.query(queryString, false)) 
                    {
                          size += Long.parseLong(qR.getPropertyValueById("cmis:contentStreamLength").toString());
                    }
                    answers.add(String.valueOf(size));
                }
                else if(CMISConstants.propertiesHashMap.containsKey(p.Name))
                    answers.add(cmisContent.getPropertyValue(CMISConstants.propertiesHashMap.get(p.Name)).toString());
                else
                    answers.add(null);
            }
        }
        else
        {
            for(Property p:request)
            {
                if(p.Name.equalsIgnoreCase("ContentType"))
                    answers.add("application/cmis-document");
                else if(CMISConstants.propertiesHashMap.containsKey(p.Name))
                    answers.add(cmisContent.getPropertyValue(CMISConstants.propertiesHashMap.get(p.Name)).toString());
                else
                    answers.add(null);
            }
        }
        XRow xRet;
        xRet = new CMISRow(answers);
        
        return xRet;
        
    }            
    
    //My method
    private Any[] setPropertyValues(PropertyValue[] pValues) throws UnknownPropertyException, IllegalArgumentException
    {
        Any ans[] = new Any[pValues.length];
        CMISPropertySetInfo xCommandInfo = new CMISPropertySetInfo(m_xContext);
        int index = 0;
        
        

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
                        String title = AnyConverter.toString(p.Value);
                    
                        Map<String,String> updateProperties = new HashMap<String,String>();
                        updateProperties.put(PropertyIds.NAME, title);
                    
                        try
                        {                                          
                            cmisContent.updateProperties(updateProperties);
                            ans[index] = null;
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
                            path = path+"/"+AnyConverter.toString(p.Value);
                            xContentid = new CMISContentIdentifier(m_xContext, path);
                            ans[index] = null;
                        }
                    }
                }   
                    
            }
            
            index ++;
        }
        return ans;
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
        XContent xContent;
        xContent = new CMISContent(m_xContext, Info.Type, path);
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

}
