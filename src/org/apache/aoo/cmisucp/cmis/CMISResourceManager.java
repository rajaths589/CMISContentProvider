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

import com.sun.star.beans.Property;
import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.io.XInputStream;
import com.sun.star.lib.uno.adapter.ByteArrayToXInputStreamAdapter;
import com.sun.star.lib.uno.adapter.XInputStreamToInputStreamAdapter;
import com.sun.star.ucb.InteractiveBadTransferURLException;
import com.sun.star.uno.Any;
import com.sun.star.uno.Type;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.DateTime;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.DocumentType;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.CapabilityContentStreamUpdates;
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
    private String url;
    private CMISConnect m_Connect;
    private Document pwc;
    private String pwc_version;
    private CMISRepoCredentials creds;
    public boolean versionable;

    public CMISResourceManager(XComponentContext xContext, CMISConnect connect) {
        object = connect.getObject();
        connected = connect.getSession();
        generateFolderorDocument();
        m_Connect = connect;
        url = connect.getURL();//not working
        initializePWC();

    }

    public CMISResourceManager(XComponentContext xContext, CmisObject obj, Session s, String mURL) {
        object = obj;
        connected = s;
        generateFolderorDocument();
        m_Connect = null;
        url = mURL;
        initializePWC();
    }

    private void initializePWC() {
        if (isCheckedOut()) {
            String pwcid = documentObject.getVersionSeriesCheckedOutId();
            pwc = (Document) connected.getObject(pwcid);
        }
    }

    /**
     *
     * @param temp
     */
    public void setRepoCredentials(CMISRepoCredentials temp) {
        creds = temp;
    }

    private void generateFolderorDocument() {
        if (isFolder()) {
            folderObject = (Folder) object;
            documentObject = null;
            isFolder = true;
            isDocument = false;
        } else if (isDocument()) {
            folderObject = null;
            documentObject = (Document) object;
            isDocument = true;
            isFolder = false;
            setIsVersionable();
        }
    }

    public OperationContext getOperationContext() {
        return connected.createOperationContext();
    }
   
    public boolean canSetContentStream() {
        if (isDocument) {
            if (connected.getRepositoryInfo().getCapabilities().getContentStreamUpdatesCapability() == CapabilityContentStreamUpdates.ANYTIME) {
                return true;
            }
        }
        return false;
    }

    private void setIsVersionable() {
        if (isDocument) {
            versionable = ((DocumentType) (getDocument().getType())).isVersionable();
        }
    }

    /**
     *
     * @return
     */
    public boolean exists() {
        if (object == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @return
     */
    public boolean isFolder() {
        if (object.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDocument() {
        if (object.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    public boolean getPrivateWorkingCopy() {
        if (isDocument) {
            if (canCheckOut()) {
                pwc = (Document) connected.getObject(getDocument().checkOut());
                pwc_version = pwc.getVersionLabel();
                return true;
            }
        }
        return false;
    }

    public String getCompleteURL() {
        return creds.URL;
    }

    public String getLatestVersion() {
        return getDocument().getObjectOfLatestVersion(false).getVersionLabel();
    }

    /**
     *
     * @return
     */
    public String getPWCVersion() {
        if (pwc != null) {
            return pwc_version;
        }

        return null;
    }

    public boolean isLatestVersion() {
        return getDocument().isLatestVersion();
    }

    public boolean checkIn(boolean isMajor, XInputStream stream, String checkinComment) throws IOException {
        if (pwc != null) {
            if (canCheckIn()) {
                try {
                    XInputStreamToInputStreamAdapter inputStream = new XInputStreamToInputStreamAdapter(stream);
                    ContentStream createContentStream = connected.getObjectFactory().createContentStream(getName(), inputStream.available(), getMimeType(), inputStream);
                    ObjectId id = pwc.checkIn(isMajor, null, createContentStream, checkinComment);
                    documentObject = (Document) connected.getObject(id);
                    pwc = null;
                    pwc_version = null;
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean canCheckOut() {
        if (isDocument) {
            if (getDocument().getAllowableActions().getAllowableActions().contains(Action.CAN_CHECK_OUT)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }
   
    public boolean canCheckIn() {
        if (isDocument) {
            if (getDocument().getAllowableActions().getAllowableActions().contains(Action.CAN_CHECK_IN)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Document getVersionedDocument(String label) {
        if (isDocument) {
            for (Document d : getDocument().getAllVersions()) {
                if (d.getVersionLabel().equalsIgnoreCase(label)) {
                    return d;
                }
            }
        }

        return null;
    }

    /**
     *
     * @return
     */
    public Folder getFolder() {
        if (isFolder) {
            return folderObject;
        } else {
            return null;
        }

    }

    public void cancelCheckOut() {
        if (pwc != null) {
            pwc.cancelCheckOut();
        }
    }

    /**
     *
     * @return
     */
    public Document getDocument() {
        if (isDocument) {
            if (pwc != null) {
                return pwc;
            } else {
                return documentObject;
            }
        } else {
            return null;
        }
    }

    public String getProperty(String propertyID) {
        try {
            return object.getPropertyValue(propertyID).toString();
        } catch (NullPointerException ex) {
            return null;
        } catch (CmisBaseException ex) {

            log.log(Level.INFO, "cmis exception:{0}", ex.getExceptionName());
            return null;
        }
    }

    /**
     *
     * @param PropertyID
     * @return
     */
    public Any getPropertyAsAny(String PropertyID) {
        if (PropertyID.equalsIgnoreCase("ID")) {
            return new Any(Type.STRING, getID());
        } else if (PropertyID.equalsIgnoreCase("Title")) {
            return new Any(Type.STRING, getName());
        } else if (PropertyID.equalsIgnoreCase("IsFolder")) {
            return new Any(Type.BOOLEAN, isFolder);
        } else if (PropertyID.equalsIgnoreCase("IsDocument")) {
            return new Any(Type.BOOLEAN, (isDocument));
        } else if (PropertyID.equalsIgnoreCase("DateCreated")) {
            return new Any(Type.VOID, getCreationDate());
        } else if (PropertyID.equalsIgnoreCase("DateModified")) {
            return new Any(Type.VOID, getLastModifiedDate());
        } else if (PropertyID.equalsIgnoreCase("MediaType")) {
            return new Any(Type.STRING, getMimeType());
        } else if (PropertyID.equalsIgnoreCase("ContentType")) {
            return new Any(Type.STRING, getContentType());
        } else if (PropertyID.equalsIgnoreCase("CreatedBy")) {
            return new Any(Type.STRING, getCreatedBy());
        } else if (PropertyID.equalsIgnoreCase("ModifiedBy")) {
            return new Any(Type.STRING, getLastModifiedBy());
        } else if (PropertyID.equalsIgnoreCase("CheckinComment")) {
            return new Any(Type.STRING, getCheckinComment());
        } else if (PropertyID.equalsIgnoreCase("Size")) {
            return new Any(Type.HYPER, getSize());
        } else if (PropertyID.equalsIgnoreCase("IsRemote")) {
            return new Any(Type.BOOLEAN, false);
        } else if (PropertyID.equalsIgnoreCase("IsFloppy")) {
            return new Any(Type.BOOLEAN, false);
        } else if (PropertyID.equalsIgnoreCase("IsCompactDisc")) {
            return new Any(Type.BOOLEAN, false);
        } else if (PropertyID.equalsIgnoreCase("IsHidden")) {
            return new Any(Type.BOOLEAN, false);
        } else if (PropertyID.equalsIgnoreCase("IsVolume")) {
            return new Any(Type.BOOLEAN, false);
        } else if (PropertyID.equalsIgnoreCase("IsRemoveable")) {
            return new Any(Type.BOOLEAN, false);
        } else if (PropertyID.equalsIgnoreCase("IsReadOnly")) {
            return new Any(Type.BOOLEAN, checkReadOnly());
        } else if (PropertyID.equalsIgnoreCase("CasePreservingURL")) {
            return new Any(Type.BOOLEAN, true);
        } else if (PropertyID.equalsIgnoreCase("TargetURL")) {
            return new Any(Type.STRING, url);
        } else if (PropertyID.equalsIgnoreCase("BaseURI")) {
            return new Any(Type.STRING, getParentPath());
        } else if (PropertyID.equalsIgnoreCase("LatestVesionLabel")) {
            return new Any(Type.STRING, getLatestVersion());
        } else if (PropertyID.equalsIgnoreCase("CurrentVersionLabel")) {
            return new Any(Type.STRING, getPWCVersion());
        } else {
            return null;
        }
    }

    private boolean checkReadOnly() {    //this condition is not always true..       
        //if(pwc!=null||canSetContentStream())
        if (versionable) {
            if (pwc != null) {
                return false;
            } else {
                return true;
            }
        } else {
            if (canSetContentStream()) {
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     *
     * @param props
     * @return
     */
    public Any[] getPropertiesAsAny(Property[] props) {
        Any ret[] = new Any[props.length];
        int index = 0;
        for (Property p : props) {
            ret[index] = getPropertyAsAny(p.Name);
            index++;
        }
        return ret;
    }

    public String getName() {
        return object.getName();
    }

    /**
     *
     * @return
     */
    public String getID() {
        return object.getId();
    }

    /**
     *
     * @return
     */
    public String getPath() {
        if (isFolder) {
            return object.getPropertyValue(PropertyIds.PATH).toString();
        } else {
            return getDocument().getPaths().get(0);
        }
    }

    public long getSize() {
        if (isDocument) {
            return getDocument().getContentStreamLength();
        } else {
            String queryString;
            queryString = "SELECT cmis:contentStreamLength FROM cmis:document WHERE IN_TREE(\'" + getID() + "\')";

            long folder_size = 0;
            for (QueryResult qR : connected.query(queryString, false)) {
                folder_size += Long.parseLong(qR.getPropertyValueById("cmis:contentStreamLength").toString());
            }

            return folder_size;
        }
    }

    /**
     *
     * @return
     */
    public String getMimeType() {
        if (isDocument) {
            return getDocument().getContentStreamMimeType();
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public String getContentType() {
        if (isDocument) {
            return "application/cmis-document";
        } else if (isFolder) {
            return "application/cmis-folder";
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public DateTime getCreationDate() {
        GregorianCalendar cal = object.getCreationDate();
        DateTime creationDate = new DateTime();

        creationDate.Day = (short) cal.get(Calendar.DAY_OF_MONTH);
        creationDate.Month = (short) cal.get(Calendar.MONTH);
        creationDate.Year = (short) cal.get(Calendar.YEAR);
        creationDate.Hours = (short) cal.get(Calendar.HOUR_OF_DAY);
        creationDate.Minutes = (short) cal.get(Calendar.MINUTE);
        creationDate.Seconds = (short) cal.get(Calendar.SECOND);
        creationDate.HundredthSeconds = (short) cal.get(Calendar.MILLISECOND);
        return creationDate;
    }

    public String getCreatedBy() {
        return object.getCreatedBy();
    }

    /**
     *
     * @return
     */
    public String getLastModifiedBy() {
        return object.getLastModifiedBy();
    }

    public String getCheckinComment() {
        if (isDocument) {
            return getDocument().getCheckinComment();
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public DateTime getLastModifiedDate() {
        GregorianCalendar cal = object.getLastModificationDate();
        DateTime creationDate = new DateTime();

        creationDate.Day = (short) cal.get(Calendar.DAY_OF_MONTH);
        creationDate.Month = (short) cal.get(Calendar.MONTH);
        creationDate.Year = (short) cal.get(Calendar.YEAR);
        creationDate.Hours = (short) cal.get(Calendar.HOUR_OF_DAY);
        creationDate.Minutes = (short) cal.get(Calendar.MINUTE);
        creationDate.Seconds = (short) cal.get(Calendar.SECOND);
        creationDate.HundredthSeconds = (short) cal.get(Calendar.MILLISECOND);
        return creationDate;
    }

    /**
     *
     * @return @throws IOException
     * @throws NotConnectedException
     * @throws com.sun.star.io.IOException
     */
    public XInputStream getInputStream() throws IOException, NotConnectedException, com.sun.star.io.IOException {
        if (isDocument) {
            InputStream inp = null;
            try {
                inp = getDocument().getContentStream().getStream();
            } catch (Exception e) {
                log.info("error");
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = inp.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return new ByteArrayToXInputStreamAdapter(buffer.toByteArray());
        } else {
            return null;
        }
    }

    public boolean setInputStream(XInputStream xInputStream) throws IOException, NotConnectedException, BufferSizeExceededException, com.sun.star.io.IOException {
        if (isDocument && canSetContentStream()) {
            XInputStreamToInputStreamAdapter inputStream = new XInputStreamToInputStreamAdapter(xInputStream);
            //byte arr[][] = new byte[1][];  
            //xInputStream.readBytes(arr, xInputStream.available());
            //InputStream inputStream = new ByteArrayInputStream(arr[0]);
            try {
                ContentStream stream = connected.getObjectFactory().createContentStream(getName(), inputStream.available(), getMimeType(), inputStream);
                Document d = getDocument().setContentStream(stream, true);
                if (d != null) {
                    documentObject = d;
                }
                return true;
            } catch (Exception ex) {
                return false;
            }
        } else {
            return false;
        }

    }

    public boolean isCheckedOut() {
        if (pwc != null) {
            return true;
        }
        if (isDocument) {
            return documentObject.isVersionSeriesCheckedOut().booleanValue();
        }

        return false;
    }

    public boolean setName(String newName) {
        Map<String, String> nameMap = new HashMap<String, String>();
        nameMap.put(PropertyIds.NAME, newName);

        try {
            object.updateProperties(nameMap);
            return true;
        } catch (CmisBaseException ex) {
            return false;
        }
    }

    // to be removed in consultation with Mentor
    public List<String> getProperties(Property[] props) {
        List<String> returnProperties = new ArrayList<String>();

        for (Property p : props) {
            if (p.Name.equalsIgnoreCase("ID")) {
                returnProperties.add(getID());
            } else if (p.Name.equalsIgnoreCase("Title")) {
                returnProperties.add(getName());
            } else if (p.Name.equalsIgnoreCase("IsFolder")) {
                returnProperties.add(String.valueOf(isFolder));
            } else if (p.Name.equalsIgnoreCase("IsDocument")) {
                returnProperties.add(String.valueOf(isDocument));
            } else if (p.Name.equalsIgnoreCase("DateCreated")) {
                returnProperties.add(getCreationDate().Day + "/" + getCreationDate().Month + "/" + getCreationDate().Year);
            } else if (p.Name.equalsIgnoreCase("DateModified")) {
                returnProperties.add(getLastModifiedDate().Day + "/" + getLastModifiedDate().Month + "/" + getLastModifiedDate().Year);
            } else if (p.Name.equalsIgnoreCase("MediaType")) {
                returnProperties.add(getMimeType());
            } else if (p.Name.equalsIgnoreCase("ContentType")) {
                returnProperties.add(getContentType());
            } else if (p.Name.equalsIgnoreCase("CreatedBy")) {
                returnProperties.add(getCreatedBy());
            } else if (p.Name.equalsIgnoreCase("ModifiedBy")) {
                returnProperties.add(getLastModifiedBy());
            } else if (p.Name.equalsIgnoreCase("CheckinComment")) {
                returnProperties.add(getCheckinComment());
            } else if (p.Name.equalsIgnoreCase("Size")) {
                returnProperties.add(String.valueOf(getSize()));
            } else {
                returnProperties.add(null);
            }
        }

        return returnProperties;
    }

    public boolean createDocument(XInputStream input, String name, String MimeType) throws IOException {
        if (isDocument) {
            return false;
        } else if (isFolder) {
            Map<String, String> newDocProps = new HashMap<String, String>();
            newDocProps.put(PropertyIds.NAME, name);
            newDocProps.put(PropertyIds.OBJECT_TYPE_ID, ObjectType.DOCUMENT_BASETYPE_ID);

            XInputStreamToInputStreamAdapter inpStream = new XInputStreamToInputStreamAdapter(input);
            //String MIMETYPE = new MimetypesFileTypeMap().getContentType(inpStream);
            ContentStream stream = connected.getObjectFactory().createContentStream(name, inpStream.available(), MimeType, inpStream);
            try {
                getFolder().createDocument(newDocProps, stream, VersioningState.NONE);
                return true;
            } catch (CmisBaseException ex) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean createFolder(String name) {
        if (isDocument) {
            return false;
        } else if (isFolder) {
            Map<String, String> newFolderProps = new HashMap<String, String>();
            newFolderProps.put(PropertyIds.NAME, name);
            newFolderProps.put(PropertyIds.OBJECT_TYPE_ID, ObjectType.FOLDER_BASETYPE_ID);
            try {
                getFolder().createFolder(newFolderProps);
                return true;
            } catch (CmisBaseException e) {
                return false;
            }
        } else {
            return false;
        }

    }

    public Folder getParent() {
        if (isFolder) {
            return getFolder().getParents().get(0); //Multi-filing unsupported.
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public String getParentPath() {
        if(creds.serverURL!=null)
        {
            String s = creds.serverURL;
            if(s.endsWith("/"))
                s = s.substring(0,s.length()-1);
            if(isFolder) 
            {
                return s+getFolder().getParents().get(0).getPath();
            }
            else 
            {
                return s+getDocument().getParents().get(0).getPath();
            }
        }
        return null;
    }
    
    public ItemIterable<CmisObject> getChildren() {
        if (isDocument) {
            return null;
        } else if (isFolder) {
            return getFolder().getChildren();
        } else {
            return null;
        }
    }

    /**
     *
     * @param name
     * @return
     */
    public CMISResourceManager getChild(String name) {
        for (CmisObject obj : getChildren()) {
            if (obj.getName().equals(name)) {
                CMISRepoCredentials childCred = creds.createChildCredentials(name);
                return CMISResourceCache.getObject(m_Context).getManager(childCred);
            }
        }

        return null;
    }

    /**
     *
     * @return
     */
    public List<CMISResourceManager> getChildrenAsManager() {
        List<CMISResourceManager> group = new ArrayList<CMISResourceManager>();
        for (CmisObject obj : getChildren()) {
            CMISRepoCredentials childCred = creds.createChildCredentials(obj.getName());
            group.add(CMISResourceCache.getObject(m_Context).getManager(childCred));
        }
        return group;
    }

    /**
     *
     * @param connect
     * @param newName
     * @throws IOException
     * @throws InteractiveBadTransferURLException
     */
    public void transfer(CMISConnect connect, String newName) throws IOException, InteractiveBadTransferURLException {
        if (isDocument) {
            return;
        }
        try {

            CMISResourceManager transferResource = new CMISResourceManager(m_Context, connect);
            if (transferResource.isDocument) {
                createDocument(transferResource.getInputStream(), newName, transferResource.getMimeType());
            } else if (transferResource.isFolder) {
                createFolder(newName);
                CMISResourceManager newTransferFolder = new CMISResourceManager(m_Context, connected.getObjectByPath(getPath() + "/" + newName), connected, url + "/" + newName);
                for (CmisObject child : transferResource.getFolder().getChildren()) {
                    //CMISConnect childConnect = new CMISConnect(m_Context, child, connected);
                    //newTransferFolder.transfer(childConnect,child.getName());                                        
                }
            }
        } catch (Exception e) {
            throw new InteractiveBadTransferURLException();
        }
    }

    /**
     *
     */
    public void delete() {
        if (isDocument) {
            getDocument().deleteAllVersions();
        }
        if (isFolder) {
            getFolder().deleteTree(true, UnfileObject.DELETE, false);
        }
    }
}
