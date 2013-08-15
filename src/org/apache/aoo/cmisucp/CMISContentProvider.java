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
package org.apache.aoo.cmisucp;

import com.sun.star.io.IOException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.ucb.ContentCreationException;
import com.sun.star.ucb.IllegalIdentifierException;
import com.sun.star.ucb.XContent;
import com.sun.star.ucb.XContentIdentifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.aoo.cmisucp.unobojects.CMISContent;
import org.apache.aoo.cmisucp.unobojects.CMISContentIdentifier;


public final class CMISContentProvider extends WeakBase
   implements com.sun.star.ucb.XParameterizedContentProvider,
              com.sun.star.lang.XServiceInfo,
              com.sun.star.ucb.XContentIdentifierFactory,
              com.sun.star.ucb.XContentProvider
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISContentProvider.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.ucb.ContentProvider" };

    //My Variables
    private Map<String,XContent> cache;

    public CMISContentProvider( XComponentContext context )
    {
        m_xContext = context;
        cache = new HashMap<String,XContent>();
    };

    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = CMISSingleComponentFactory.getComponentFactory(m_implementationName,m_serviceNames);            
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

    // com.sun.star.ucb.XParameterizedContentProvider:
    public com.sun.star.ucb.XContentProvider registerInstance(String Template, String Arguments, boolean ReplaceExisting) throws com.sun.star.lang.IllegalArgumentException
    {
        // TODO: Exchange the default return implementation for "registerInstance" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return null;
    }

    public com.sun.star.ucb.XContentProvider deregisterInstance(String Template, String Arguments) throws com.sun.star.lang.IllegalArgumentException
    {
        // TODO: Exchange the default return implementation for "deregisterInstance" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return null;
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
         return m_implementationName;
    }

    public boolean supportsService( String sService ) {
        int len = m_serviceNames.length;

        for( int i=0; i < len; i++) {
            if (sService.equals(m_serviceNames[i]))
                return true;
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    // com.sun.star.ucb.XContentIdentifierFactory:
    public com.sun.star.ucb.XContentIdentifier createContentIdentifier(String ContentId)
    {
        XContentIdentifier xContentIdentifier = new CMISContentIdentifier(m_xContext, ContentId);
        return xContentIdentifier;
    }

    // com.sun.star.ucb.XContentProvider:
    public com.sun.star.ucb.XContent queryContent(com.sun.star.ucb.XContentIdentifier Identifier) throws com.sun.star.ucb.IllegalIdentifierException
    {

        if(!isLegalIdentifier(Identifier))
            throw new IllegalIdentifierException(Identifier.getContentIdentifier()+" is illegal");
        
        XContent xRet = queryExistingIdentifier(Identifier);
        if(xRet!=null)
            return xRet;
        
        CMISContent cmisObj = null;
        try {
            cmisObj = new CMISContent(m_xContext, Identifier);
        } catch (ContentCreationException ex) {
            return null;
        } catch (NotConnectedException ex) {
            Logger.getLogger(CMISContentProvider.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CMISContentProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        registerNewContent(cmisObj,Identifier);            
        
        
        return cmisObj;
    }

    // My method
    private boolean isLegalIdentifier(XContentIdentifier id)
    {
        //To-Do
        return true;
    }
    //My method
    private XContent queryExistingIdentifier(XContentIdentifier id)
    {
        if(cache.containsKey(id.getContentIdentifier()))
            return cache.get(id.getContentIdentifier());
        
        return null;
    }
    //My method
    private void registerNewContent(XContent xContent, XContentIdentifier xContentIdentifier)
    {
        cache.put(xContentIdentifier.getContentIdentifier(), xContent);
    }
    
    public int compareContentIds(com.sun.star.ucb.XContentIdentifier Id1, com.sun.star.ucb.XContentIdentifier Id2)
    {
        String id1 = Id1.getContentIdentifier();
        String id2 = Id2.getContentIdentifier();
        return id1.compareTo(id2);
    }

}
