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
package org.apache.aoo.cmisucp.helper;

import com.sun.star.beans.PropertyAttribute;
import com.sun.star.io.XInputStream;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lib.uno.helper.PropertySet;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.sdbc.FetchDirection;
import com.sun.star.sdbc.ResultSetConcurrency;
import com.sun.star.sdbc.ResultSetType;
import com.sun.star.sdbc.SQLException;
import com.sun.star.sdbc.XResultSet;
import com.sun.star.sdbc.XResultSetMetaData;
import com.sun.star.ucb.AlreadyInitializedException;
import com.sun.star.ucb.ListenerAlreadySetException;
import com.sun.star.ucb.ServiceNotFoundException;
import com.sun.star.ucb.XDynamicResultSet;
import com.sun.star.ucb.XDynamicResultSetListener;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.Date;
import com.sun.star.util.DateTime;
import com.sun.star.util.Time;
import java.util.logging.Logger;

public class ContentPropertySet extends PropertySet
   implements com.sun.star.lang.XServiceInfo,
              com.sun.star.sdbc.XResultSetMetaDataSupplier,
              com.sun.star.sdbc.XResultSet,
              com.sun.star.sdbc.XRow,
              com.sun.star.sdbc.XCloseable,
              com.sun.star.ucb.XContentAccess,

              // optional but important
              com.sun.star.sdbc.XResultSetUpdate,
              com.sun.star.sdbc.XRowUpdate,
              com.sun.star.sdbc.XWarningsSupplier,
              com.sun.star.sdbc.XColumnLocate,
              
              // for "com.sun.star.ucb.DynamicResultSet" service
              com.sun.star.ucb.XDynamicResultSet {

    private final XComponentContext m_xContext;
    // class is not registered in UNO; just prepare it
    private static final String m_implementationName = ContentPropertySet.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.sdbc.ResultSet",
        "com.sun.star.ucb.ContentResultSet",
        //"com.sun.star.ucb.DynamicResultSet"
        };

    private static final Logger log = Logger.getLogger(ContentPropertySet.class.getName());
    
    // property
    // optional
    protected String m_CursorName;
    protected int m_ResultSetConcurrency;
    protected int m_ResultSetType;
    protected int m_FetchDirection;
    protected int m_FetchSize;
//    protected int m_CursorTravelMode;
    
    protected int m_RowCount;
    protected boolean m_IsRowCountFinal;
    private int m_Index;
    private PropertyAndValueSet[] m_PropertyAndValueSet;
    
    private ContentPropertySet(XComponentContext context) {
        m_xContext = context;
        m_ResultSetConcurrency = ResultSetConcurrency.UPDATABLE;
        m_ResultSetType = ResultSetType.FORWARD_ONLY;
        m_FetchDirection = FetchDirection.FORWARD;
        m_IsRowCountFinal = true;
        m_Index = -1;
        // TO-DO check the cursor name and make it dynamic from the content provider scheme
        m_CursorName = new String("cmis.CMISCursor");
        registerProperty("CursorName", "m_CursorName",
              (short)0);
        registerProperty("ResultSetConcurrency", "m_ResultSetConcurrency",
              (short)(PropertyAttribute.MAYBEVOID|PropertyAttribute.BOUND|PropertyAttribute.MAYBEDEFAULT|PropertyAttribute.REMOVEABLE|PropertyAttribute.OPTIONAL));
        registerProperty("ResultSetType", "m_ResultSetType",
              (short)(PropertyAttribute.MAYBEVOID|PropertyAttribute.BOUND|PropertyAttribute.MAYBEDEFAULT|PropertyAttribute.REMOVEABLE|PropertyAttribute.OPTIONAL));
        registerProperty("FetchDirection", "m_FetchDirection",
              (short)0);
        registerProperty("FetchSize", "m_FetchSize",
              (short)0);
//        registerProperty("CursorTravelMode", "m_CursorTravelMode",
//              PropertyAttribute.BOUND);
        registerProperty("RowCount", "m_RowCount",
              (short)(PropertyAttribute.MAYBEVOID|PropertyAttribute.BOUND|PropertyAttribute.MAYBEDEFAULT|PropertyAttribute.REMOVEABLE|PropertyAttribute.OPTIONAL));
        registerProperty("IsRowCountFinal", "m_IsRowCountFinal",
              (short)(PropertyAttribute.MAYBEVOID|PropertyAttribute.BOUND|PropertyAttribute.MAYBEDEFAULT|PropertyAttribute.REMOVEABLE|PropertyAttribute.OPTIONAL));

    }

    public ContentPropertySet(XComponentContext context, PropertyAndValueSet[] properties) {
        this(context);
        m_RowCount = properties.length;
        m_PropertyAndValueSet = properties;
    }
    
    // implement but class is not registered yet
    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) ) {
            xFactory = Factory.createComponentFactory(ContentPropertySet.class, m_serviceNames);
        }
        return xFactory;
    }

    // implement but class is not registered yet
    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
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

    // com.sun.star.sdbc.XResultSet:
    public boolean next() throws com.sun.star.sdbc.SQLException
    {
        if (m_Index < -1) {
            m_Index = -1;
        }
        if (m_Index < m_RowCount) {
            m_Index++;
        }
        return(m_Index < m_RowCount);
    }

    public boolean isBeforeFirst() throws com.sun.star.sdbc.SQLException
    {
        return m_Index < 0;
    }

    public boolean isAfterLast() throws com.sun.star.sdbc.SQLException
    {
        return m_Index >= m_RowCount;
    }

    public boolean isFirst() throws com.sun.star.sdbc.SQLException
    {
        return m_Index == 0;
    }

    public boolean isLast() throws com.sun.star.sdbc.SQLException
    {
        return m_Index == m_RowCount - 1;
    }

    public void beforeFirst() throws com.sun.star.sdbc.SQLException
    {
        m_Index = -1;
    }

    public void afterLast() throws com.sun.star.sdbc.SQLException
    {
        m_Index = m_RowCount;
    }

    public boolean first() throws com.sun.star.sdbc.SQLException
    {
        m_Index = 0;
        return true;
    }

    public boolean last() throws com.sun.star.sdbc.SQLException
    {
        m_Index = m_RowCount - 1;
        return true;
    }
    

    public int getRow() throws com.sun.star.sdbc.SQLException
    {
        return m_Index + 1;
    }

    public boolean absolute(int row) throws com.sun.star.sdbc.SQLException
    {
        if (row < 0 ) {
            m_Index = m_RowCount + row;
        }
        else {
            m_Index = row - 1;
        }
        if (m_Index >= 0 && m_Index < m_RowCount) {
            return true;
        }
        return true;
    }

    public boolean relative(int rows) throws com.sun.star.sdbc.SQLException
    {
        m_Index += rows;
        return true;
    }

    public boolean previous() throws com.sun.star.sdbc.SQLException
    {
        m_Index--;
        return true;
    }

    public void refreshRow() throws com.sun.star.sdbc.SQLException
    {
    }

    public boolean rowUpdated() throws com.sun.star.sdbc.SQLException
    {
        return false;
    }

    public boolean rowInserted() throws com.sun.star.sdbc.SQLException
    {
        return false;
    }

    public boolean rowDeleted() throws com.sun.star.sdbc.SQLException
    {
        return false;
    }

    public Object getStatement() throws com.sun.star.sdbc.SQLException
    {
        log.info("get statement");
        return null;
    }

    // com.sun.star.sdbc.XResultSetMetaDataSupplier:
    public XResultSetMetaData getMetaData() throws SQLException {
        log.info(" -> get Meta Data");
        // TODO: implement ContentMetaData class reasonable ...
        return new ContentMetaData(m_PropertyAndValueSet[0].getProperties());
    }

    // com.sun.star.ucb.XContentAccess:
    public String queryContentIdentifierString()
    {
        return m_PropertyAndValueSet[m_Index].getUrl();
    }

    public com.sun.star.ucb.XContentIdentifier queryContentIdentifier()
    {
        log.info("query content identifier");
        return null;
    }

    public com.sun.star.ucb.XContent queryContent()
    {
        log.info("query content");
        return null;
    }

    // com.sun.star.sdbc.XRow:
    public boolean wasNull() throws com.sun.star.sdbc.SQLException
    {
        return false;
    }

    public String getString(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getString(columnIndex);
    }

    public boolean getBoolean(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getBoolean(columnIndex);
    }

    public byte getByte(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getByte(columnIndex);
    }

    public short getShort(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getShort(columnIndex);
    }

    public int getInt(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getInt(columnIndex);
    }

    public long getLong(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getLong(columnIndex);
    }

    public float getFloat(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getFloat(columnIndex);
    }

    public double getDouble(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getDouble(columnIndex);
    }

    public byte[] getBytes(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getBytes(columnIndex);
    }

    public com.sun.star.util.Date getDate(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getDate(columnIndex);
    }

    public com.sun.star.util.Time getTime(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getTime(columnIndex);
    }

    public DateTime getTimestamp(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        return m_PropertyAndValueSet[m_Index].getTimestamp(columnIndex);
    }

    public com.sun.star.io.XInputStream getBinaryStream(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getBinaryStream" !!!
        log.info("get binary stream");
        return m_PropertyAndValueSet[m_Index].getBinaryStream(columnIndex);
    }

    public com.sun.star.io.XInputStream getCharacterStream(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getCharacterStream" !!!
        log.info("get character stream");
        return m_PropertyAndValueSet[m_Index].getCharacterStream(columnIndex);
    }

    public Object getObject(int columnIndex, com.sun.star.container.XNameAccess typeMap) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getObject" !!!
        log.info("######### ContentResultSet getObject " + (typeMap!=null?typeMap.getElementNames().length:"null"));
        return m_PropertyAndValueSet[m_Index].getObject(columnIndex, typeMap);
    }

    public com.sun.star.sdbc.XRef getRef(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getRef" !!!
        log.info("get ref");
        return m_PropertyAndValueSet[m_Index].getRef(columnIndex);
    }

    public com.sun.star.sdbc.XBlob getBlob(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getBlob" !!!
        log.info("get blob");
        return m_PropertyAndValueSet[m_Index].getBlob(columnIndex);
    }

    public com.sun.star.sdbc.XClob getClob(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getClob" !!!
        log.info("get clob");
        return m_PropertyAndValueSet[m_Index].getClob(columnIndex);
    }

    public com.sun.star.sdbc.XArray getArray(int columnIndex) throws com.sun.star.sdbc.SQLException
    {
        // TODO: Exchange the default return implementation for "getArray" !!!
        log.info("get array");
        return m_PropertyAndValueSet[m_Index].getArray(columnIndex);
    }

    // com.sun.star.sdbc.XCloseable:
    public void close() throws com.sun.star.sdbc.SQLException
    {
        log.info("close");
    }

    
    
    
    

    
    
    

    public void insertRow() throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateRow() throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void deleteRow() throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void cancelRowUpdates() throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void moveToInsertRow() throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void moveToCurrentRow() throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateNull(int arg0) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateBoolean(int arg0, boolean arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateByte(int arg0, byte arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateShort(int arg0, short arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateInt(int arg0, int arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateLong(int arg0, long arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateFloat(int arg0, float arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateDouble(int arg0, double arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateString(int arg0, String arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateBytes(int arg0, byte[] arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateDate(int arg0, Date arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateTime(int arg0, Time arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateTimestamp(int arg0, DateTime arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateBinaryStream(int arg0, XInputStream arg1, int arg2) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateCharacterStream(int arg0, XInputStream arg1, int arg2) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateObject(int arg0, Object arg1) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void updateNumericObject(int arg0, Object arg1, int arg2) throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public Object getWarnings() throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
        return null;
    }

    public void clearWarnings() throws SQLException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public int findColumn(String columnName) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return m_PropertyAndValueSet[m_Index].findColumn(columnName);
    }

    public XResultSet getStaticResultSet() throws ListenerAlreadySetException {
        return this;
    }

    public void setListener(XDynamicResultSetListener arg0) throws ListenerAlreadySetException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }

    public void connectToCache(XDynamicResultSet arg0) throws ListenerAlreadySetException, AlreadyInitializedException, ServiceNotFoundException {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
    }


    public short getCapabilities() {
        StackTraceElement[] elements = new Throwable().fillInStackTrace().getStackTrace(); System.out.println (elements[0]); System.out.flush();
        return 0;
    }

}

