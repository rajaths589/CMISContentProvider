package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.beans.Property;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.ComponentBase;
import com.sun.star.sdbc.SQLException;
import com.sun.star.sdbc.XResultSet;
import com.sun.star.sdbc.XRow;
import com.sun.star.ucb.XContentIdentifier;
import java.util.List;


public final class CMISDynamicResultSet extends ComponentBase
   implements com.sun.star.ucb.XDynamicResultSet,
              com.sun.star.lang.XServiceInfo
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISDynamicResultSet.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.ucb.DynamicResultSet" };

    private List<XRow> values;
    private Property[] props;
    private XContentIdentifier xID;
    
    public CMISDynamicResultSet( XComponentContext context , List<XRow> arg, Property[] argP, XContentIdentifier argID )
    {
        m_xContext = context;
        values = arg;
        props = argP;
        xID = argID;
    };

    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(CMISDynamicResultSet.class, m_serviceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

    // com.sun.star.ucb.XDynamicResultSet:
    public com.sun.star.sdbc.XResultSet getStaticResultSet() throws com.sun.star.ucb.ListenerAlreadySetException
    {
        XResultSet xResultSet = new CMISContentResultSet(m_xContext,values,props,xID);
        return xResultSet;
    }

    public void setListener(com.sun.star.ucb.XDynamicResultSetListener Listener) throws com.sun.star.ucb.ListenerAlreadySetException
    {
        // TODO: Insert your implementation for "setListener" here.
    }

    public void connectToCache(com.sun.star.ucb.XDynamicResultSet Cache) throws com.sun.star.ucb.ListenerAlreadySetException, com.sun.star.ucb.AlreadyInitializedException, com.sun.star.ucb.ServiceNotFoundException
    {
        // TODO: Insert your implementation for "connectToCache" here.
    }

    public short getCapabilities()
    {
        // TODO: Exchange the default return implementation for "getCapabilities" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return 0;
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

}
