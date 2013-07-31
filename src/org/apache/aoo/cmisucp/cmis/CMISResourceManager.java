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
package org.apache.aoo.cmisucp.cmis;

import com.sun.star.beans.Property;
import com.sun.star.io.XInputStream;
import com.sun.star.lib.uno.adapter.XInputStreamToInputStreamAdapter;
import com.sun.star.ucb.InteractiveBadTransferURLException;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.Date;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.aoo.cmisucp.unobojects.CMISInputStream;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;

/**
 *
 * @author rajath
 */
public class CMISResourceManager {
    
    public XComponentContext m_Context;
    private CmisObject object;
    private Session connected;   
    private Folder folderObject;
    private Document documentObject;
    public boolean isFolder;
    public boolean isDocument;
    private static final Logger log = Logger.getLogger(CMISResourceManager.class.getName());
    
    public CMISResourceManager(XComponentContext xContext, CmisObject ob, Session s)
    {        
        object = ob;
        connected = s;
        generateFolderorDocument();    
    }
    
    private void generateFolderorDocument()
    {
        if(isFolder())
        {
            folderObject = (Folder)object;
            documentObject = null;
            isFolder = true;
            isDocument = false;
        }
        else if(isDocument())
        {
            folderObject = null;
            documentObject = (Document)object;
            isDocument = true;
            isFolder = false;
        }
    }
    
    public boolean exists()
    {
        if(object==null)        
            return false;
        else
            return true;
    }
    
    public boolean isFolder()
    {
        if(object.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER))
            return true;
        else
            return false;
    }
    
    public boolean isDocument()
    {
        if(object.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT))
            return true;
        else
            return false;
    }
    
    public boolean canCheckOut()
    {
        if(isDocument)
        {
            if(getDocument().getAllowableActions().getAllowableActions().contains(Action.CAN_CHECK_OUT))
                return true;
            else
                return false;
        }
        else
            return false;
        
    }
    
    public boolean canCheckIn()
    {
        if(isDocument)
        {
            if(getDocument().getAllowableActions().getAllowableActions().contains(Action.CAN_CHECK_IN))
                return true;
            else
                return false;
        }
        else
            return false;
    }
    
    public Document getVersionedDocument(String label)
    {
        if(isDocument)
            for(Document d:getDocument().getAllVersions())
                if(d.getVersionLabel().equalsIgnoreCase(label))
                    return d;
        
        return null;
    }
    
    
    public Folder getFolder()
    {        
        if(isFolder)
            return folderObject;
        else
            return null;
            
    }
    
    public Document getDocument()
    {
        if(isDocument)
            return documentObject;
        else
            return null;            
    }
    
    public String getProperty(String propertyID)
    {
        try
        {
            return object.getPropertyValue(propertyID).toString();
        }
        catch(NullPointerException ex)
        {
            return null;
        }
    }
    
    public String getName()
    {
        return object.getName();
    }
    
    public String getID()
    {
        return object.getId();
    }
    
    public String getPath()
    {
        if(isFolder)
            return object.getPropertyValue(PropertyIds.PATH).toString();
        else
        {
            return getDocument().getPaths().get(0);
        }
    }
    
    public long getSize()
    {
        if(isDocument)
            return getDocument().getContentStreamLength();
        else
        {
            String queryString;
            queryString = "SELECT cmis:contentStreamLength FROM cmis:document WHERE IN_TREE(\'"+getID()+"\')";
            
            long folder_size = 0;
            for(QueryResult qR:connected.query(queryString, false))
            {
                folder_size += Long.parseLong(qR.getPropertyValueById("cmis:contentStreamLength").toString());
            }
            
            return folder_size;
        }
    }
    
    public String getMimeType()
    {
        if(isDocument)
            return getDocument().getContentStreamMimeType();
        else
            return null;
    }
    
    public String getContentType()
    {
        if(isDocument)
            return "application/cmis-document";
        else if(isFolder)
            return "application/cmis-folder";
        else
            return null;
    }
    
    public Date getCreationDate()
    {
        GregorianCalendar cal = object.getCreationDate();
        Date creationDate = new Date();
        
        creationDate.Day = (short) cal.get(Calendar.DAY_OF_MONTH);
        creationDate.Month = (short) cal.get(Calendar.MONTH);
        creationDate.Year = (short) cal.get(Calendar.YEAR);
        
        return creationDate;
    }
    
    public String getCreatedBy()
    {
        return object.getCreatedBy();
    }
    
    public String getLastModifiedBy()
    {
        return object.getLastModifiedBy();
    }
    
    public String getCheckinComment()
    {        
        if(isDocument)
            return getDocument().getCheckinComment();
        else
            return null;
    }
    
    public Date getLastModifiedDate()
    {
        GregorianCalendar cal = object.getLastModificationDate();
        Date modifiedDate = new Date();
        
        modifiedDate.Day = (short) cal.get(Calendar.DAY_OF_MONTH);
        modifiedDate.Month = (short) cal.get(Calendar.MONTH);
        modifiedDate.Year = (short) cal.get(Calendar.YEAR);
        
        return modifiedDate;
    }
    
    public XInputStream getInputStream()
    {
        if(isDocument)
        {
            //com.sun.star.lib.uno.adapter.InputStreamToXInputStreamAdapter xInputStream;
            //xInputStream = new InputStreamToXInputStreamAdapter(getDocument().getContentStream().getStream());
            XInputStream xInputStream = new CMISInputStream(m_Context, getDocument());
            return xInputStream;
        }
        else
            return null;
    }
    
    public boolean setInputStream(XInputStream xInputStream) throws IOException
    {
        if(isDocument)
        {
            XInputStreamToInputStreamAdapter inputStream = new XInputStreamToInputStreamAdapter(xInputStream);
            try
            {
                ContentStream stream = connected.getObjectFactory().createContentStream(getName(), inputStream.available(),getMimeType(), inputStream);
                getDocument().setContentStream(stream, true);
                return true;
            }
            catch(Exception ex)
            {
                return false;
            }
        }
        else
            return false;
        
    }
        
    public boolean setName(String newName)
    {
        Map<String,String> nameMap = new HashMap<String, String>();
        nameMap.put(PropertyIds.NAME, newName);
        
        try
        {
            object.updateProperties(nameMap);
            return true;
        }
        catch(CmisBaseException ex)
        {
            return false;
        }
    }
    
    public  List<String> getProperties(Property[] props)
    {
        List<String> returnProperties = new ArrayList<String>();
        
        for(Property p:props)
        {            
            if(p.Name.equalsIgnoreCase("ID"))
                returnProperties.add(getID());
            else if(p.Name.equalsIgnoreCase("Title"))
                returnProperties.add(getName());
            else if(p.Name.equalsIgnoreCase("IsFolder"))
                returnProperties.add(String.valueOf(isFolder));
            else if(p.Name.equalsIgnoreCase("IsDocument"))
                returnProperties.add(String.valueOf(isDocument));
            else if(p.Name.equalsIgnoreCase("DateCreated"))
                returnProperties.add(getCreationDate().Day+"/"+getCreationDate().Month+"/"+getCreationDate().Year);
            else if(p.Name.equalsIgnoreCase("DateModified"))
                returnProperties.add(getLastModifiedDate().Day+"/"+getLastModifiedDate().Month+"/"+getLastModifiedDate().Year);
            else if(p.Name.equalsIgnoreCase("MediaType"))
                returnProperties.add(getMimeType());
            else if(p.Name.equalsIgnoreCase("ContentType"))
                returnProperties.add(getContentType());
            else if(p.Name.equalsIgnoreCase("CreatedBy"))
                returnProperties.add(getCreatedBy());
            else if(p.Name.equalsIgnoreCase("ModifiedBy"))
                returnProperties.add(getLastModifiedBy());
            else if(p.Name.equalsIgnoreCase("CheckinComment"))
                returnProperties.add(getCheckinComment());
            else if(p.Name.equalsIgnoreCase("Size"))
                returnProperties.add(String.valueOf(getSize()));
            else
                returnProperties.add(null);
        }
        
        return returnProperties;
    }
    
    public boolean createDocument(XInputStream input, String name, String MimeType) throws IOException
    {
        if(isDocument)
            return false;
        else if(isFolder)
        {
            Map<String,String> newDocProps = new HashMap<String, String>();
            newDocProps.put(PropertyIds.NAME, name);
            newDocProps.put(PropertyIds.OBJECT_TYPE_ID, ObjectType.DOCUMENT_BASETYPE_ID);
            
            XInputStreamToInputStreamAdapter inpStream = new XInputStreamToInputStreamAdapter(input);
            ContentStream stream = connected.getObjectFactory().createContentStream(name, inpStream.available(),MimeType, inpStream);
            try
            {
                getFolder().createDocument(newDocProps, stream, VersioningState.NONE);
                return true;
            }
            catch(CmisBaseException ex)
            {
                return false;
            }
        }
        else
            return false;
    }    
    
    public boolean createFolder(String name)
    {
        if(isDocument)
            return false;
        else if(isFolder)
        {
            Map<String,String> newFolderProps = new HashMap<String, String>();
            newFolderProps.put(PropertyIds.NAME, name);
            newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, ObjectType.FOLDER_BASETYPE_ID);
            try
            {                           
                getFolder().createFolder(newFolderProps);
                return true;
            }
            catch(CmisBaseException e)
            {
                return false;
            }
        }
        else
            return false;
                
    }
    
    public Folder getParent()
    {        
        if(isFolder)
            return getFolder().getParents().get(0); //Multi-filing unsupported.
        else
            return null;
    }
    
    public ItemIterable<CmisObject> getChildren()
    {
        if(isDocument)
            return null;
        else if(isFolder)
            return getFolder().getChildren();
        else
            return null;
    }
        
    
    public void transfer(CmisObject transferObject, Session transferSession, String newName ) throws IOException, InteractiveBadTransferURLException
    {
        if(isDocument)
            return;
        try
        {            
        
            CMISResourceManager transferResource = new CMISResourceManager(m_Context, transferObject, transferSession);
            if(transferResource.isDocument)
            {
                createDocument(transferResource.getInputStream(), newName, transferResource.getMimeType());                            
            }
            else if(transferResource.isFolder)
            {
                createFolder(newName);
                CMISResourceManager newTransferFolder = new CMISResourceManager(m_Context, connected.getObjectByPath(getPath()+"/"+newName), connected);
                for(CmisObject child:transferResource.getFolder().getChildren())
                {                                
                    newTransferFolder.transfer(child, connected,child.getName());                                        
                }            
            }
        }
        catch(Exception e)
        {            
            throw new InteractiveBadTransferURLException();            
        }
    }    
    
    public void delete()
    {        
        if(isDocument)
            getDocument().deleteAllVersions();
        if(isFolder)
            getFolder().deleteTree(true, UnfileObject.DELETE, false);            
    }
}


