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
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.sdbc.ColumnType;
import com.sun.star.sdbc.ColumnValue;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class CMISResultSetMetaData extends WeakBase
   implements com.sun.star.sdbc.XResultSetMetaData
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISResultSetMetaData.class.getName();
    private int columnCount;
    private String columnNames[];

    public CMISResultSetMetaData( XComponentContext context, Property[] arg1 )
    {
        m_xContext = context;
        columnCount = arg1.length;
        columnNames = new String[arg1.length];
        
        int index = 0;
        for(Property p:arg1)
        {
            columnNames[index] = p.Name;
            index++;
        }
    };

    // com.sun.star.sdbc.XResultSetMetaData:
    public int getColumnCount() throws com.sun.star.sdbc.SQLException
    {
        
        return columnCount;
    }

    public boolean isAutoIncrement(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "isAutoIncrement" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        
        return false;
    }

    public boolean isCaseSensitive(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "isCaseSensitive" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

    public boolean isSearchable(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "isSearchable" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

    public boolean isCurrency(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "isCurrency" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

    public int isNullable(int column) throws com.sun.star.sdbc.SQLException
    {
        for(String s:columnNames)
        {
            if(s.equalsIgnoreCase("MediaType"))
                return ColumnValue.NULLABLE;
            else
                return ColumnValue.NO_NULLS;
        }
        return 0;
    }

    public boolean isSigned(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "isSigned" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

    public int getColumnDisplaySize(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getColumnDisplaySize" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return 0;
    }

    public String getColumnLabel(int column) throws com.sun.star.sdbc.SQLException
    {        
        return columnNames[column-1];
    }

    public String getColumnName(int column) throws com.sun.star.sdbc.SQLException
    {
        return columnNames[column-1];
    }

    public String getSchemaName(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getSchemaName" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new String();
    }

    public int getPrecision(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getPrecision" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return 0;
    }

    public int getScale(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getScale" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return 0;
    }

    public String getTableName(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getTableName" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new String();
    }

    public String getCatalogName(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getCatalogName" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new String();
    }

    public int getColumnType(int column) throws com.sun.star.sdbc.SQLException
    {        
        return 0;
    }

    public String getColumnTypeName(int column) throws com.sun.star.sdbc.SQLException
    {
        CMISPropertySetInfo xPSinfo = new CMISPropertySetInfo(m_xContext);
        try {
            Property p = xPSinfo.getPropertyByName(columnNames[column-1]);
            return p.Type.getTypeName();
        } catch (UnknownPropertyException ex) {
            Logger.getLogger(CMISResultSetMetaData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new String();
    }

    public boolean isReadOnly(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "isReadOnly" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return true;
    }

    public boolean isWritable(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "isWritable" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

    public boolean isDefinitelyWritable(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "isDefinitelyWritable" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

    public String getColumnServiceName(int column) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getColumnServiceName" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new String();
    }

}
