/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.aoo.cmisucp.cmis;

import java.util.HashMap;
import java.util.Map;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

/**
 *
 * @author rajath
 */
public class CMISConnect {
    
    public String url;
    public String username;
    private String password;
    public String repositoryID;
    
    private Session connected_session;
    private Folder root;
    private CmisObject content;
    
    private String contentType;
    
    public CMISConnect( String address, String user, String pwd, String repo_id , String relative_path )
    {
        url = address;
        password = pwd;
        username = user;
        repositoryID = repo_id;
        
        Map<String,String> session_parameter = new HashMap<String, String>();
        
        session_parameter.put(SessionParameter.ATOMPUB_URL, address);
        session_parameter.put(SessionParameter.USER, username);
        session_parameter.put(SessionParameter.PASSWORD, password);
        session_parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        session_parameter.put(SessionParameter.REPOSITORY_ID, repositoryID);
        
        SessionFactory factory = SessionFactoryImpl.newInstance();
        
        connected_session = factory.createSession(session_parameter);
        root = connected_session.getRootFolder();
        
        content = connected_session.getObjectByPath(relative_path);
        contentType = content.getBaseTypeId().value();
        
    }
    
    public Session getSession()
    {
        return connected_session;
    }
    
    public CmisObject getObject()
    {
        return content;
    }
    
    public String getContentType()
    {
        return contentType;
    }
}
