/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.aoo.cmisucp.cmis;

import com.sun.star.uno.XComponentContext;
import java.util.HashMap;
import java.util.Map;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;

/**
 *
 * @author rajath
 */
public class CMISConnect {
    
    public String server_url;
    public String username;
    private String password;
    public String repositoryID;
    
    private Session connected_session;
    private Folder root;
    private CmisObject content;
    
    private String contentType;
    private XComponentContext m_Context;
    
    private final String cmisHTTP = "cmis";
    private final String cmisHTTPS = "cmiss";
    
    // To be removed
    public CMISConnect(XComponentContext context, String address, String user, String pwd, String repo_id , String relative_path )
    {
        m_Context = context;
        server_url = address;
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
    
    public CMISConnect(XComponentContext xComponentContext, String uri, String user, String pwd )
    {
        m_Context = xComponentContext;
        username = user;
        password = pwd;
        decodeURI(uri);
        contentType = content.getBaseTypeId().value();
    }
        
    private boolean connectToRepository( String address, String repoID )
    {
        Map<String,String> sessionParameters = new HashMap<String, String>();
        
        //Authentication parameters
        sessionParameters.put(SessionParameter.USER, username);
        sessionParameters.put(SessionParameter.PASSWORD, password);
        
        //Repository Information
       sessionParameters.put(SessionParameter.ATOMPUB_URL, address);
       sessionParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
       sessionParameters.put(SessionParameter.REPOSITORY_ID, repoID);
       
       Session session;
       
       try
       {
          SessionFactory factory = SessionFactoryImpl.newInstance();
          session = factory.createSession(sessionParameters);
          server_url = address;
          repositoryID = repoID;
       }
       catch(CmisBaseException ex)           
       {
          return false;
       }
       
       connected_session = session;
                     
       return true;
    }
            
    private void decodeURI(String URI)
    {
        if(URI.startsWith(cmisHTTP))
            URI = URI.replaceFirst(cmisHTTP, "http");
        else if(URI.startsWith(cmisHTTPS))
            URI = URI.replaceFirst(cmisHTTPS, "https");
                    
        int prompt = URI.indexOf("://")+3;
        int indexOfServerPath = URI.indexOf('/', prompt);
        int indexOfRepoID = URI.indexOf('/', indexOfServerPath+1);
        while(!connectToRepository(URI.substring(0, indexOfServerPath),URI.substring(indexOfServerPath+1, indexOfRepoID)))        
        {
            prompt = indexOfServerPath;
            indexOfServerPath = URI.indexOf('/', prompt+1);
            indexOfRepoID = URI.indexOf('/', indexOfServerPath+1);
        }        
        if(URI.startsWith("path", indexOfRepoID+1))
        {
            String localpath = URI.substring(URI.indexOf('=')+1);
            // support for multiple parameters
            // for versioning
            connectToObject(localpath);
        }
        //support for object id
        
    }
    private void connectToObject(String path)
    {        
        try
        {
            content = connected_session.getObjectByPath(path);            
            contentType = content.getBaseTypeId().value();
        }
        catch(CmisBaseException ex)
        {
                    content = null;
                    contentType = null;
        }
    }
    private void connectToID(String id)
    {
        try
        {
            content = connected_session.getObject(id);
            contentType = content.getBaseTypeId().value();
        }
        catch(CmisBaseException ex)
        {
              content = null;
              contentType = null;
        }
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
