package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;


public final class CMISSContentIdentifier extends WeakBase
   implements com.sun.star.ucb.XContentIdentifier
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISSContentIdentifier.class.getName();
    
    private final String scheme = "cmiss";
    private final String id;

    public CMISSContentIdentifier( XComponentContext context, String contentID )
    {
        m_xContext = context;
        id = contentID;
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
