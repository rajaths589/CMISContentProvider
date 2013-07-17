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
package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.io.XInputStream;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;


public final class CMISActiveDataSink extends WeakBase
   implements com.sun.star.io.XActiveDataSink
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISActiveDataSink.class.getName();
    
    private XInputStream xInputStream;

    public CMISActiveDataSink( XComponentContext context )
    {
        m_xContext = context;                
        //xInputStream = new CMISInputStream(context, doc);
    };

    // com.sun.star.io.XActiveDataSink:
    public void setInputStream(com.sun.star.io.XInputStream aStream)
    {
        xInputStream = aStream;
    }

    public com.sun.star.io.XInputStream getInputStream()
    {         
        return xInputStream;
    }

}
