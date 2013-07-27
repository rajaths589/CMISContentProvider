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

import com.sun.star.beans.Property;
import com.sun.star.beans.PropertyAttribute;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.uno.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class CMISPropertySetInfo extends WeakBase
   implements com.sun.star.beans.XPropertySetInfo
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISPropertySetInfo.class.getName();
    private List<Property> available_properties = new ArrayList<Property>();
    
    public CMISPropertySetInfo( XComponentContext context )
    {
        m_xContext = context;
        available_properties = new ArrayList<Property>();
        addProperties();
        
    };

    public CMISPropertySetInfo( XComponentContext context, Property[] argProp )
    {
        m_xContext = context;
        available_properties = new ArrayList<Property>();
        available_properties.addAll(Arrays.asList(argProp));
    }
    //My method
    private void addProperties()
    {
        available_properties.add(new Property("Title", -1, Type.STRING, (short)0));
        available_properties.add(new Property("IsFolder",-1,Type.BOOLEAN,PropertyAttribute.READONLY));
        available_properties.add(new Property("IsDocument", -1, Type.BOOLEAN, PropertyAttribute.READONLY));
        available_properties.add(new Property("DateCreated", -1, Type.VOID, PropertyAttribute.READONLY));
        available_properties.add(new Property("DateModified",-1,Type.VOID, PropertyAttribute.READONLY));
        available_properties.add(new Property("Size", -1, Type.LONG, PropertyAttribute.READONLY));
        available_properties.add(new Property("MediaType", -1,Type.STRING, PropertyAttribute.READONLY));
        available_properties.add(new Property("ContentType", -1, Type.STRING, PropertyAttribute.READONLY));
        available_properties.add(new Property("ID", -1, Type.STRING, PropertyAttribute.READONLY));
        available_properties.add(new Property("CreatedBy", -1, Type.STRING, PropertyAttribute.READONLY));
        available_properties.add(new Property("ModifiedBy", -1, Type.STRING, PropertyAttribute.READONLY));
        available_properties.add(new Property("CheckinComment", -1,Type.STRING,PropertyAttribute.READONLY));        
    }            
    
    // com.sun.star.beans.XPropertySetInfo:
    public com.sun.star.beans.Property[] getProperties()
    {
        final int size = available_properties.size();
        Property arrayP[] = new Property[size];
        int k = 0;
        for(Property p:available_properties)
        {
           arrayP[k] = p;
           k++;                    
        }
        return arrayP;
    }

    public com.sun.star.beans.Property getPropertyByName(String aName) throws com.sun.star.beans.UnknownPropertyException
    {
        for(Property p:available_properties)
            if(p.Name.equalsIgnoreCase(aName))
                return p;
        
        throw new UnknownPropertyException(aName+" Not found");
    }

    public boolean hasPropertyByName(String Name)
    {
        for(Property p:available_properties)
            if(p.Name.equalsIgnoreCase(Name))
                return true;
        
        return false;
    }

}
