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

    private String server_url;
    private String username;
    private String password;
    private String repositoryID;
    private String nameObj;
    private String parentURL;
    private String parentName;
    private String repositoryURL;
    private String url;
    private String parent_localpath;
    private boolean authStatus = true;
    private Session connected_session;
    private Folder root;
    private CmisObject content;
    private String contentType;
    private XComponentContext m_Context;
    private final String cmisHTTP = "cmis";
    private final String cmisHTTPS = "cmiss";

    // To be removed
    public CMISConnect(XComponentContext context, String address, String user, String pwd, String repo_id, String relative_path) {
        m_Context = context;
        server_url = address;
        password = pwd;
        username = user;
        repositoryID = repo_id;

        Map<String, String> session_parameter = new HashMap<String, String>();

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

    public CMISConnect(XComponentContext xContext, String uri, String user, String pwd, String serverURL) {
        m_Context = xContext;
        username = user;
        password = pwd;
        url = uri;
        decodeURI(uri, serverURL);
        contentType = content.getBaseTypeId().value();
    }

    public CMISConnect(XComponentContext xContext, CmisObject ob, Session s, String serverURL, String uri) {
        m_Context = xContext;
        content = ob;
        connected_session = s;
        url = uri;
        repositoryURL = serverURL;
    }

    public CMISConnect(XComponentContext xComponentContext, String uri, String user, String pwd) {
        m_Context = xComponentContext;
        username = user;
        password = pwd;
        url = uri;
        decodeURI(uri);
        contentType = content.getBaseTypeId().value();
    }

    private boolean connectToRepository(String address, String repoID) {
        Map<String, String> sessionParameters = new HashMap<String, String>();

        //Authentication parameters
        sessionParameters.put(SessionParameter.USER, username);
        sessionParameters.put(SessionParameter.PASSWORD, password);

        //Repository Information
        sessionParameters.put(SessionParameter.ATOMPUB_URL, address);
        sessionParameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        sessionParameters.put(SessionParameter.REPOSITORY_ID, repoID);

        Session session;

        try {
            SessionFactory factory = SessionFactoryImpl.newInstance();
            session = factory.createSession(sessionParameters);
            server_url = address;
            repositoryID = repoID;
        } catch (CmisBaseException ex) {
            if (ex.getClass().getName().equalsIgnoreCase("org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException")) {
                authStatus = false;
            }
            return false;
        }

        connected_session = session;

        return true;
    }

    /**
     *
     * @return
     */
    public boolean getAuthStatus() {
        return authStatus;
    }

    public String getURL() {
        return url;
    }

    /*
     private void decodeURI(String URI)        
     {        
     if(URI.startsWith(cmisHTTP))
     {
     repositoryURL = "cmis://";
     URI = URI.replaceFirst(cmisHTTP, "http");                              
     }
     else if(URI.startsWith(cmisHTTPS))
     {
     repositoryURL = "cmiss://";
     URI = URI.replaceFirst(cmisHTTPS, "https");                
     }               
     if(URI.endsWith("/"))
     URI = URI.substring(0,URI.length()-1);
        
     int prompt = URI.indexOf("://")+3;      
     int indexOfServerPath = URI.indexOf('/', prompt);
     int indexOfRepoID = URI.indexOf('/', indexOfServerPath+1);
     while(!connectToRepository(URI.substring(0, indexOfServerPath),URI.substring(indexOfServerPath+1, indexOfRepoID)))        
     {
     prompt = indexOfServerPath;
     indexOfServerPath = URI.indexOf('/', prompt+1);
     indexOfRepoID = URI.indexOf('/', indexOfServerPath+1);
     if(indexOfRepoID<0)
     {    
     URI = URI+"/";
     indexOfRepoID = URI.indexOf('/', indexOfServerPath+1);
     }
     if(authStatus==false)
     {                
     break;
     }
     }          
     String localpath = URI.substring(indexOfRepoID);
     repositoryURL = URI.substring(0,indexOfRepoID);
     repositoryURL = repositoryURL.replaceFirst("http", "cmis");
     int lastSlash = URI.lastIndexOf('/');
     nameObj = URI.substring(lastSlash+1);
     parentURL = URI.substring(0, lastSlash);
     parentURL = parentURL.replaceFirst("http", "cmis");
     if(localpath.equals("/.")||localpath.equals("."))
     {
     localpath = "/";
     }
     /* if(parentURL.equals(repositoryURL))
     {
     parentURL = parentURL+"/";
     }*/
    /*
     connectToObject(localpath);
        
     }*/
    private void decodeURI(String URI, String server) {
        URI = URI.replace("%20", " ");
        if (server.startsWith(cmisHTTP)) {
            server = server.replaceFirst(cmisHTTP, "http");
        } else if (server.startsWith(cmisHTTPS)) {
            server = server.replaceFirst(cmisHTTPS, "https");
        }
        if (URI.startsWith(cmisHTTP)) {
            URI = URI.replaceFirst(cmisHTTP, "http");
        } else if (URI.startsWith(cmisHTTPS)) {
            URI = URI.replaceFirst(cmisHTTPS, "https");
        }
        if (server.endsWith(".")) {
            server = server.substring(0, server.length() - 1);
        }

        if (server.endsWith("/")) {
            server = server.substring(0, server.length() - 1);
        }

        if (URI.endsWith(".")) {
            URI = URI.substring(0, URI.length() - 1);
        }

        if (URI.endsWith("/")) {
            URI = URI.substring(0, URI.length() - 1);
        }
        int repositorySlash = server.lastIndexOf("/");
        if (connectToRepository(server.substring(0, repositorySlash), server.substring(repositorySlash + 1))) {

            server = server.replaceFirst("http", "cmis");
            repositoryURL = server;
            //repositoryURL = URI;          
            try {
                String localpath;
                localpath = URI.substring(server.length());
                if (localpath == null || localpath.equals("") || localpath.equals(".")) {
                    localpath = "/";
                }
                content = connected_session.getObjectByPath(localpath);
                contentType = content.getBaseTypeId().value();
            } catch (CmisBaseException ex) {
                content = null;
                contentType = null;
            }
        }

    }

    private void decodeURI(String URI) {
        URI = URI.replace("%20", " ");
        if (URI.startsWith(cmisHTTP)) {
            URI = URI.replaceFirst(cmisHTTP, "http");
        } else if (URI.startsWith(cmisHTTPS)) {
            URI = URI.replaceFirst(cmisHTTPS, "https");
        }

        if (URI.endsWith(".")) {
            URI = URI.substring(0, URI.length() - 1);
        }

        if (URI.endsWith("/")) {
            URI = URI.substring(0, URI.length() - 1);
        }

        //int prompt = URI.indexOf("://")+3;  
        int repositorySlash = URI.lastIndexOf("/");
        if (connectToRepository(URI.substring(0, repositorySlash), URI.substring(repositorySlash + 1))) {
            repositoryURL = URI.replaceFirst("http://", "cmis://");
            //repositoryURL = URI;          
            try {
                content = connected_session.getObjectByPath("/");
                contentType = content.getBaseTypeId().value();
            } catch (CmisBaseException ex) {
                content = null;
                contentType = null;
            }
        }
    }

    private void showMessageBox() {
    }

    private void connectToObject(String path) {
        if (authStatus == true) {
            try {
                content = connected_session.getObjectByPath(path);
                contentType = content.getBaseTypeId().value();
            } catch (CmisBaseException ex) {
                content = null;
                contentType = null;
            }
        }
    }

    private void connectToID(String id) {
        try {
            content = connected_session.getObject(id);
            contentType = content.getBaseTypeId().value();
        } catch (CmisBaseException ex) {
            content = null;
            contentType = null;
        }
    }

    public Session getSession() {
        return connected_session;
    }

    /**
     *
     * @return
     */
    public CmisObject getObject() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public String getParentURL() {
        return parentURL;
    }

    public String getRepositoryURL() {
        return repositoryURL;
    }
}
