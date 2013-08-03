/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.aoo.cmisucp.helper;

import com.sun.star.beans.Property;
import com.sun.star.sdbc.DataType;
import com.sun.star.sdbc.SQLException;
import com.sun.star.sdbc.XResultSetMetaData;

public class ContentMetaData implements XResultSetMetaData {

    private Property[] props;

    public ContentMetaData(Property[] props) {
        this.props = props;
    }
    
    public int getColumnCount() throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return props.length;
    }

    public boolean isAutoIncrement(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return false;
    }

    public boolean isCaseSensitive(int arg0) throws SQLException {
//        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        Property prop = props[arg0 - 1];
        if (prop.Name.equals("Title"))
            return true;
        return false;  // cannot see why IsFolder should be case sensitive
    }

    public boolean isSearchable(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return false;
    }

    public boolean isCurrency(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return false;
    }

    public int isNullable(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return 0;
    }

    public boolean isSigned(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return true;
    }

    public int getColumnDisplaySize(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return 2;
    }

    public String getColumnLabel(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return "Name";
    }

    public String getColumnName(int arg0) throws SQLException {
        String name = props[arg0 - 1].Name;
//        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return name;
    }

    public String getSchemaName(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return "scheme";
    }

    public int getPrecision(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return 0;
    }

    public int getScale(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return 2;
    }

    public String getTableName(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return "table1";
    }

    public String getCatalogName(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return "cat";
    }

    public int getColumnType(int arg0) throws SQLException {
        int retValue = DataType.OBJECT;  // no other idea
        Property prop = props[arg0 - 1];
        if (prop.Name.equals("Title")) {
            retValue = DataType.VARCHAR;
        }
        else if (prop.Name.equals("IsFolder")) {
            retValue = DataType.BOOLEAN;
        }
        return retValue;
    }

    public String getColumnTypeName(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return "col1";
    }

    public boolean isReadOnly(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return false;
    }

    public boolean isWritable(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return true;
    }

    public boolean isDefinitelyWritable(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return true;
    }

    public String getColumnServiceName(int arg0) throws SQLException {
        System.out.println (new Throwable().fillInStackTrace().getStackTrace()[0]); System.out.flush();
        return "col service";
    }
    
}