package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.io.BufferSizeExceededException;
import com.sun.star.io.NotConnectedException;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import java.io.IOException;
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
        int size = 0;
        
        try 
        {
            size = stream.read(aData[0], 0, nBytesToRead);
        }
        catch (IOException ex) 
        {
            throw new com.sun.star.io.IOException();          
        }
        catch(NullPointerException ex)
        {
            throw new NotConnectedException();
        }
        catch(ArrayIndexOutOfBoundsException ex)
        {
            throw new BufferSizeExceededException();
        }
        return size;                       
    }

    public int readSomeBytes(byte[][] aData, int nMaxBytesToRead) throws com.sun.star.io.NotConnectedException, com.sun.star.io.BufferSizeExceededException, com.sun.star.io.IOException
    {        
        return readBytes(aData, nMaxBytesToRead);        
        
    }

    public void skipBytes(int nBytesToSkip) throws com.sun.star.io.NotConnectedException, com.sun.star.io.BufferSizeExceededException, com.sun.star.io.IOException
    {
        try {
            stream.skip(nBytesToSkip);
        } catch (IOException ex) {
            throw new com.sun.star.io.IOException();
        }
    }

    public int available() throws com.sun.star.io.NotConnectedException, com.sun.star.io.IOException
    {
        try {
            return stream.available();
        } catch (IOException ex) {
            throw new com.sun.star.io.IOException();
        }
    }

    public void closeInput() throws com.sun.star.io.NotConnectedException, com.sun.star.io.IOException
    {
        try {
            stream.close();
        } catch (IOException ex) {
            throw new com.sun.star.io.IOException();
        }
    }    

}
