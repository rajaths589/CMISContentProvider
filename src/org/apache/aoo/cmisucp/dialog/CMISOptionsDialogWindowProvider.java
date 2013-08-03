package org.apache.aoo.cmisucp.dialog;

import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;


public final class CMISOptionsDialogWindowProvider extends WeakBase
   implements com.sun.star.awt.XContainerWindowProvider,
              com.sun.star.lang.XServiceInfo
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISOptionsDialogWindowProvider.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.awt.ContainerWindowProvider" };


    public CMISOptionsDialogWindowProvider( XComponentContext context )
    {
        m_xContext = context;
    };

    public static XSingleComponentFactory __getComponentFactory( String sImplementationName ) {
        XSingleComponentFactory xFactory = null;

        if ( sImplementationName.equals( m_implementationName ) )
            xFactory = Factory.createComponentFactory(CMISOptionsDialogWindowProvider.class, m_serviceNames);
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo( XRegistryKey xRegistryKey ) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                                                m_serviceNames,
                                                xRegistryKey);
    }

    // com.sun.star.awt.XContainerWindowProvider:
    public com.sun.star.awt.XWindow createContainerWindow(String URL, String WindowType, com.sun.star.awt.XWindowPeer xParent, Object xHandler) throws com.sun.star.lang.IllegalArgumentException
    {        
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

}
