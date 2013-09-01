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

public class CMISRepoCredentials 
{
    public final String URL;
    private final String username;
    private final String password;
    public String serverURL;
    public CMISRepoCredentials(String url, String user, String pwd)
    {
        URL = url;
        username = user;
        password = pwd;
    }
    
    public CMISConnect getConnect(XComponentContext xContent)
    {
        CMISConnect connect = new CMISConnect(xContent, URL, username, password);
        setServerURL(connect.getRepositoryURL());
        return connect;
    }
    
    public void setServerURL(String url)
    {
        serverURL = url;
    }
    
    public boolean startsWithServer(CMISRepoCredentials compare)
    {            
        if(username.equalsIgnoreCase(compare.username))
        {
            if(password.equalsIgnoreCase(compare.password))
            {
                if(compare.serverURL!=null)
                {
                    if((URL.startsWith(compare.serverURL)))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public CMISRepoCredentials createChildCredentials(String childName)
    {
        if(URL.endsWith("/"))
        {
            return new CMISRepoCredentials(URL+childName, username, password);
        }
        else
        {
            return new CMISRepoCredentials(URL+"/"+childName, username, password);
        }
    }
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof CMISRepoCredentials)
        {
            CMISRepoCredentials cred = (CMISRepoCredentials)o;       
            if(cred.URL.equals(URL)&&cred.username.equals(username)&&cred.password.equals(password))
            {
                return true;
            }                   
        }
        return false;
    }      

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.URL != null ? this.URL.hashCode() : 0);
        hash = 97 * hash + (this.username != null ? this.username.hashCode() : 0);
        hash = 61 * hash + (this.password != null ? this.password.hashCode() : 0);
        return hash;
    }
}
