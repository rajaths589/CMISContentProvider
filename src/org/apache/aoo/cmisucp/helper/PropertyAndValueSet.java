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

import com.sun.star.beans.Property;
import com.sun.star.container.XNameAccess;
import com.sun.star.io.XInputStream;
import com.sun.star.lib.uno.adapter.InputStreamToXInputStreamAdapter;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.sdbc.SQLException;
import com.sun.star.sdbc.XArray;
import com.sun.star.sdbc.XBlob;
import com.sun.star.sdbc.XClob;
import com.sun.star.sdbc.XColumnLocate;
import com.sun.star.sdbc.XRef;
import com.sun.star.sdbc.XRow;
import com.sun.star.uno.Any;
import com.sun.star.uno.AnyConverter;
import com.sun.star.util.Date;
import com.sun.star.util.DateTime;
import com.sun.star.util.Time;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PropertyAndValueSet extends WeakBase implements XRow, XColumnLocate {

    //private static HashMap<String, PropertyAndValueSet> thePropValueSet = new HashMap<String, PropertyAndValueSet>();
    private ArrayList<PropertyAndValue> m_PropValues;
    private String m_URL;
    private static final Logger log = Logger.getLogger(PropertyAndValueSet.class.getName());

    public PropertyAndValueSet(String url) {
        m_PropValues = new ArrayList<PropertyAndValue>();
        m_URL = url;
    }

    public void updateProperties(Property[] props, Any[] values) {
        if (props.length != values.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < values.length; i++) {
            Any value = values[i];
            Property property = props[i];
            PropertyAndValue pav = new PropertyAndValue();
            pav.theProperty = property;
            pav.value = value;
            m_PropValues.add(pav);
        }
    }

    // own methods
    /**
     *
     * @return
     */
    public Property[] getProperties() {
        // take any property and value array
        Property[] props = new Property[m_PropValues.size()];
        int index = 0;
        for (PropertyAndValue pav : m_PropValues) {
            props[index++] = pav.theProperty;
        }
        return props;
    }

    /**
     *
     * @return
     */
    public String getUrl() {
        return m_URL;
    }

    // Implement XRow
    public boolean wasNull() throws SQLException {
        log.info("PVS was null:");
        return false;
    }

    private Any getValueAsAny(int columnIndex, XNameAccess arg1) throws IndexOutOfBoundsException {
        PropertyAndValue pav = m_PropValues.get(columnIndex - 1);
        if (arg1 != null) {
            log.log(Level.INFO, "{0}Get Object With NameAccess {1}", new Object[]{m_URL, arg1.getElementNames().length});
        }
        if (pav.value != null) {
            log.log(Level.INFO, "{0}Get Object {1} {2}", new Object[]{m_URL, pav.theProperty.Name, pav.value.toString()});
        } else {
            log.log(Level.INFO, "{0}#### Get Null Object {1}", new Object[]{m_URL, pav.theProperty.Name});
        }
        return pav.value;
    }

    private <T> T getValueAsType(int columnIndex, Class<T> type) throws SQLException {
        String errorMessage = null;
        try {
            Any any = getValueAsAny(columnIndex, null);
            T result = (T) AnyConverter.toObject(type, any);
            return result;
        } catch (com.sun.star.lang.IllegalArgumentException ex) {
            errorMessage = ex.getMessage();
        } catch (IndexOutOfBoundsException ex) {
            errorMessage = ex.getMessage();
        }
        throw new SQLException(errorMessage);
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public String getString(int columnIndex) throws SQLException {
        return getValueAsType(columnIndex, String.class);
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public boolean getBoolean(int columnIndex) throws SQLException {
        return getValueAsType(columnIndex, Boolean.class); // outboxing
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public byte getByte(int columnIndex) throws SQLException {
        return getValueAsType(columnIndex, Byte.class); // outboxing
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public short getShort(int columnIndex) throws SQLException {
        return getValueAsType(columnIndex, Short.class); // outboxing
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public int getInt(int columnIndex) throws SQLException {
        return getValueAsType(columnIndex, Integer.class); // outboxing
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public long getLong(int columnIndex) throws SQLException {
        return getValueAsType(columnIndex, Long.class); // outboxing
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public float getFloat(int columnIndex) throws SQLException {
        return getValueAsType(columnIndex, Float.class); // outboxing
    }

    public double getDouble(int columnIndex) throws SQLException {
        return getValueAsType(columnIndex, Double.class); // outboxing
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public byte[] getBytes(int columnIndex) throws SQLException {
        return getString(columnIndex).getBytes();
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public Date getDate(int columnIndex) throws SQLException {
        Any anyDate = getValueAsAny(columnIndex, null);
        DateTime date = (DateTime) anyDate.getObject();
        return new Date(date.Day, date.Month, date.Year);
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public Time getTime(int columnIndex) throws SQLException {
        Any anyDate = getValueAsAny(columnIndex, null);
        DateTime date = (DateTime) anyDate.getObject();
        return new Time(date.Hours, date.Minutes, date.Seconds, date.HundredthSeconds);
    }

    public DateTime getTimestamp(int columnIndex) throws SQLException {
        Any anyDate = getValueAsAny(columnIndex, null);
        return (DateTime) anyDate.getObject();
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public XInputStream getBinaryStream(int columnIndex) throws SQLException {
        InputStream inStream = new ByteArrayInputStream(getBytes(columnIndex));
        return new InputStreamToXInputStreamAdapter(inStream);
    }

    public XInputStream getCharacterStream(int columnIndex) throws SQLException {
        return getBinaryStream(columnIndex);
    }

    public Object getObject(int columnIndex, XNameAccess arg1) throws SQLException {
        String errorMessage = null;
        try {
            Any value = getValueAsAny(columnIndex, arg1);
            if (value != null) {
                return value.getObject();
            } else {
                errorMessage = "unknown property.";
            }
        } catch (IndexOutOfBoundsException ex) {
            errorMessage = ex.getMessage();
        }
        throw new SQLException(errorMessage);
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public XRef getRef(int columnIndex) throws SQLException {
        log.info("Not supported yet.");
        return null;
    }

    /**
     *
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    public XBlob getBlob(int columnIndex) throws SQLException {
        log.info("Not supported yet.");
        return null;
    }

    public XClob getClob(int columnIndex) throws SQLException {
        log.info("Not supported yet.");
        return null;
    }

    public XArray getArray(int columnIndex) throws SQLException {
        log.info("Not supported yet.");
        return null;
    }
    // finished XRow

    // XColumnLocate 
    public int findColumn(String columnIndex) throws SQLException {
        log.log(Level.INFO, "#### Find Column: {0} ####", columnIndex);
        int index = 0;
        for (PropertyAndValue pav : m_PropValues) {
            if (pav.theProperty.Name.equals(columnIndex)) {
                index = m_PropValues.indexOf(pav) + 1; // index starts with 1
                break; // exit for loop when index is found
            }
        }
        return index;
    }

    // relation property - value
    private class PropertyAndValue {

        protected Property theProperty;
        protected Any value;

        @Override
        public String toString() {
            return theProperty.Name;
        }
    }
}