package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import java.io.InputStream;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;


public final class CMISInputStream extends WeakBase
   implements com.sun.star.io.XInputStream
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISInputStream.class.getName();
    private Document doc;
    private InputStream stream;
    
    public CMISInputStream( XComponentContext context, Document arg )
    {
        m_xContext = context;
        doc = arg;
        getInputStream();
    };
    
    private void getInputStream()
    {
        ContentStream contentStream = doc.getContentStream();
        stream = contentStream.getStream();
    }
    // com.sun.star.io.XInputStream:
    public int readBytes(byte[][] aData, int nBytesToRead) throws com.sun.star.io.NotConnectedException, com.sun.star.io.BufferSizeExceededException, com.sun.star.io.IOException
    {
        return 0;
                       
    }

    public int readSomeBytes(byte[] aData, int nMaxBytesToRead) throws com.sun.star.io.NotConnectedException, com.sun.star.io.BufferSizeExceededException, com.sun.star.io.IOException
    {
        // TODO: Exchange the default return implementation for "readSomeBytes" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return 0;
    }

    public void skipBytes(int nBytesToSkip) throws com.sun.star.io.NotConnectedException, com.sun.star.io.BufferSizeExceededException, com.sun.star.io.IOException
    {
        // TODO: Insert your implementation for "skipBytes" here.
    }

    public int available() throws com.sun.star.io.NotConnectedException, com.sun.star.io.IOException
    {
        // TODO: Exchange the default return implementation for "available" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return 0;
    }

    public void closeInput() throws com.sun.star.io.NotConnectedException, com.sun.star.io.IOException
    {
        // TODO: Insert your implementation for "closeInput" here.
    }

    public int readSomeBytes(byte[][] arg0, int arg1) throws NotConnectedException, BufferSizeExceededException, com.sun.star.io.IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
