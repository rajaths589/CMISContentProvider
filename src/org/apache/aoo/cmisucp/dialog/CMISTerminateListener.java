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
package org.apache.aoo.cmisucp.dialog;

import com.sun.star.frame.TerminationVetoException;
import com.sun.star.frame.XTerminateListener;
import com.sun.star.lang.EventObject;
import org.apache.aoo.cmisucp.cmis.CMISResourceManager;

public class CMISTerminateListener implements XTerminateListener{

    private CMISResourceManager resourceManager;
    public CMISTerminateListener(CMISResourceManager manager)
    {
        resourceManager = manager;
    }
    
    public void queryTermination(EventObject arg0) throws TerminationVetoException {
        if(resourceManager.isCheckedOut())
        {
            throw new TerminationVetoException("CMIS Document Checkedout!!!", this);            
        }
    }

    public void notifyTermination(EventObject arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void disposing(EventObject arg0) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
