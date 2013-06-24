/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.aoo.cmisucp.cmis;

import com.sun.star.beans.Property;
import com.sun.star.io.XInputStream;
import com.sun.star.lib.uno.adapter.InputStreamToXInputStreamAdapter;
import com.sun.star.lib.uno.adapter.XInputStreamToInputStreamAdapter;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.Date;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
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
        }
        else if(isDocument())
        {
            folderObject = null;
            documentObject = (Document)object;
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
    
    public Folder getFolder()
    {
        if(isFolder())
            return folderObject;
        else
            return null;
            
    }
    
    public Document getDocument()
    {
        if(isDocument())
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
        if(isFolder())
            return object.getPropertyValue(PropertyIds.PATH).toString();
        else
        {
            return getDocument().getPaths().get(0);
        }
    }
    
    public long getSize()
    {
        if(isDocument())
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
        if(isDocument())
            return getDocument().getContentStreamMimeType();
        else
            return null;
    }
    
    public String getContentType()
    {
        if(isDocument())
            return "application/cmis-document";
        else if(isFolder())
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
        if(isDocument())
        {
            com.sun.star.lib.uno.adapter.InputStreamToXInputStreamAdapter xInputStream;
            xInputStream = new InputStreamToXInputStreamAdapter(getDocument().getContentStream().getStream());
            return xInputStream;
        }
        else
            return null;
    }
    
    public boolean setInputStream(XInputStream xInputStream) throws IOException
    {
        if(isDocument())
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
                returnProperties.add(String.valueOf(isFolder()));
            else if(p.Name.equalsIgnoreCase("IsDocument"))
                returnProperties.add(String.valueOf(isFolder()));
            else if(p.Name.equalsIgnoreCase("DateCreated"))
                returnProperties.add(getCreationDate().Day+"/"+getCreationDate().Month+"/"+getCreationDate().Year);
            else if(p.Name.equalsIgnoreCase("DateModified"))
                returnProperties.add(getLastModifiedDate().Day+"/"+getLastModifiedDate().Month+"/"+getLastModifiedDate().Year);
            else if(p.Name.equalsIgnoreCase("MediaType"))
                returnProperties.add(getMimeType());
            else if(p.Name.equalsIgnoreCase("ContentType"))
                returnProperties.add(getContentType());
            else
                returnProperties.add(null);
        }
        
        return returnProperties;
    }
    
    public boolean createDocument(XInputStream input, String name, String MimeType) throws IOException
    {
        if(isDocument())
            return false;
        else if(isFolder())
        {
            Map<String,String> newDocProps = new HashMap<String, String>();
            newDocProps.put(PropertyIds.NAME, name);
            newDocProps.put(PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
            
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
        if(isDocument())
            return false;
        else if(isFolder())
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
        if(isFolder())
            return getFolder().getParents().get(0); //Multi-filing unsupported.
        else
            return null;
    }
}


