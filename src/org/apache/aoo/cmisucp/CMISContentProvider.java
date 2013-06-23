package org.apache.aoo.cmisucp;

import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.sdbc.SQLException;
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
            xFactory = Factory.createComponentFactory(CMISContentProvider.class, m_serviceNames);
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
        
        CMISContent cmisObj;
        
        cmisObj = new CMISContent(m_xContext, Identifier);
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
        //TO-DO
        return 0;
    }

}
