package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.io.XInputStream;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;


public final class CMISActiveDataSink extends WeakBase
   implements com.sun.star.io.XActiveDataSink
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISActiveDataSink.class.getName();
    private CmisObject cmisObj;
    private Document doc;
    private XInputStream xInputStream;

    public CMISActiveDataSink( XComponentContext context, CmisObject arg0, Session arg1 )
    {
        m_xContext = context;        
        cmisObj = arg0;
        doc = (Document)cmisObj;
        xInputStream = new CMISInputStream(context, doc);
    };

    // com.sun.star.io.XActiveDataSink:
    public void setInputStream(com.sun.star.io.XInputStream aStream)
    {
        xInputStream = aStream;
    }

    public com.sun.star.io.XInputStream getInputStream()
    {
        
        return xInputStream;
    }

}
