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
import org.apache.chemistry.opencmis.client.api.Session;

public class CMISSessionCache {
    
    private Map<CMISRepoCredentials,CMISConnect> connectCache;
    private static CMISSessionCache obj;
    
    private CMISSessionCache()
    {
        connectCache = new HashMap<CMISRepoCredentials,CMISConnect>();    
    }
    
    private void addConnect(CMISConnect connect,CMISRepoCredentials credentials)
    {
        connectCache.put(credentials, connect);
    }
    
    public CMISConnect getConnect(XComponentContext context, CMISRepoCredentials credentials)
    {
        for(CMISRepoCredentials c:connectCache.keySet())
        {
            if(credentials.equals(c))
            {
                return connectCache.get(c);
            }
        }
        for(CMISRepoCredentials c:connectCache.keySet())
        {
            if(credentials.startsWithServer(c))
            {
                CMISConnect similarConnect = connectCache.get(c);
                Session sameSession = similarConnect.getSession();
                String relative_path;
                try
                {
                    relative_path = credentials.URL.substring(c.serverURL.length());
                }
                catch(StringIndexOutOfBoundsException ex)
                {
                    relative_path = "/";
                }
                if(relative_path.equals("/.")||relative_path.equals(".")||relative_path.equals(""))
                {
                    relative_path = "/";
                }
                
                CmisObject obj1 = sameSession.getObjectByPath(relative_path);
                CMISConnect connect = new CMISConnect(context, obj1 , sameSession);
                connectCache.put(credentials, connect);
                return connect;
            }
        }
        
        CMISConnect connect = credentials.getConnect(context);
        connectCache.put(credentials, connect);
        return connect;
    }
    
    public static CMISSessionCache obtainSessionCache()
    {
        if(obj!=null)
            return obj;
        else
        {
            obj = new CMISSessionCache();
            return obj;
        }
    }
}
