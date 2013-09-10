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

import com.sun.star.task.XInteractionHandler;
import com.sun.star.uno.XComponentContext;
import java.util.HashMap;
import java.util.Map;

public class CMISResourceCache {

    private XComponentContext m_xContext;
    private Map<CMISRepoCredentials, CMISResourceManager> cmisCache;
    private static CMISResourceCache singleObj;

    private CMISResourceCache(XComponentContext xContext) {
        cmisCache = new HashMap<CMISRepoCredentials, CMISResourceManager>();
        m_xContext = xContext;
    }

    /**
     *
     * @param xContext
     * @return
     */
    public static CMISResourceCache getObject(XComponentContext xContext) {
        if (singleObj == null) {
            singleObj = new CMISResourceCache(xContext);
        }
        return singleObj;
    }

    public CMISResourceManager getManager(CMISRepoCredentials repo) {
        if (cmisCache.containsKey(repo)) {
            return cmisCache.get(repo);
        } else {
            CMISSessionCache cache = CMISSessionCache.obtainSessionCache();
            CMISConnect connect = cache.getConnect(m_xContext, repo);
            repo.serverURL = connect.getRepositoryURL();
            CMISResourceManager rManager = new CMISResourceManager(m_xContext, connect);
            rManager.setRepoCredentials(repo);
            cmisCache.put(repo, rManager);
            return rManager;
        }
    }
}
