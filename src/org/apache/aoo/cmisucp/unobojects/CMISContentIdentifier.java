package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;


public final class CMISContentIdentifier extends WeakBase
   implements com.sun.star.ucb.XContentIdentifier
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISContentIdentifier.class.getName();
    
    //My Variables
    private final String scheme = "cmis";
    private final String id;

    public CMISContentIdentifier( XComponentContext context, String ContentID )
    {
        m_xContext = context;
        
        id = ContentID;
    };

    // com.sun.star.ucb.XContentIdentifier:
    public String getContentIdentifier()
    {
        return id;
    }

    public String getContentProviderScheme()
    {
        return scheme;
    }

}
