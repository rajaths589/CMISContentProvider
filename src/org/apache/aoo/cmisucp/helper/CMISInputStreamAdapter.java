package org.apache.aoo.cmisucp.helper;

import java.io.IOException;
import com.sun.star.io.XInputStream;
import java.io.InputStream;

/**	The <code>InputStreamToInputXStreamAdapter</code> wraps the 
	Java <code>InputStream</code> object into a 
	UNO <code>XInputStream</code> object.  
	This allows users to access an <code>InputStream</code> 
	as if it were an <code>XInputStream</code>.
 */
public class CMISInputStreamAdapter implements XInputStream {

    /** 
     *  Internal store to the InputStream
     */
    private InputStream iIn;
 
    /**
     *  Constructor.
     *
     *  @param  in  The <code>XInputStream</code> to be 
     *              accessed as an <code>InputStream</code>.
     */
    public CMISInputStreamAdapter (InputStream in) 
	{
        iIn = in;
    }

    public int available() throws 
			com.sun.star.io.IOException
	{

        int bytesAvail;

        try {
            bytesAvail = iIn.available();
        } catch (IOException e) {
            throw new com.sun.star.io.IOException(e.toString());
        }

        return(bytesAvail);
    }

    public void closeInput() throws 
			com.sun.star.io.IOException 
	{
        try {
            iIn.close();
        } catch (IOException e) {
            throw new com.sun.star.io.IOException(e.toString());
        }
    }

    public int readBytes(byte[][] b, int len) throws 
			com.sun.star.io.IOException 
	{
        int count = 0;
        byte bytes[];   
        final int size;
        try {
	    long bytesRead=0;
	    if (len >iIn.available()) {
                size = iIn.available();
                bytes = new byte[size];
                bytesRead = iIn.read(bytes, 0, iIn.available());
                b[0] = bytes;
	    }
	    else{
                size = len;
                bytes = new byte[size];
		bytesRead = iIn.read(bytes, 0, len);
                b[0] = bytes;
	    }
            // Casting bytesRead to an int is okay, since the user can
            // only pass in an integer length to read, so the bytesRead 
            // must <= len.
            //
           if (bytesRead <= 0) {
                return 0;
	    } 	    
	    return ((int)bytesRead);
	    
		
        } catch (IOException e) {
            throw new com.sun.star.io.IOException("reader error: "+e.toString());
        }
    }

    public int readSomeBytes(byte[][] b, int len) throws 
			com.sun.star.io.IOException 
	{
        int count = 0;
        try {
	    long bytesRead=0;
	    if (len >iIn.available()) {
			bytesRead = iIn.read(b[0], 0, iIn.available());
	    }
	    else{
			bytesRead = iIn.read(b[0], 0, len);
	    }
            // Casting bytesRead to an int is okay, since the user can
            // only pass in an integer length to read, so the bytesRead 
            // must <= len.
            //
            if (bytesRead <= 0) {
                return(0);
	    } 	    
	    return ((int)bytesRead);
	    
		
        } catch (IOException e) {
            throw new com.sun.star.io.IOException("reader error: "+e.toString());
        }
    }

    public void skipBytes(int n) throws 
			com.sun.star.io.IOException 
	{
        int avail;
        int tmpLongVal = n;
        int  tmpIntVal;

        try {
            avail = iIn.available();
        } catch (IOException e) {
            throw new com.sun.star.io.IOException(e.toString());
        }

        do {
            if (tmpLongVal >= Integer.MAX_VALUE) {
               tmpIntVal = Integer.MAX_VALUE;
            } else {
               // Casting is safe here.
               tmpIntVal = (int)tmpLongVal;
            }
            tmpLongVal -= tmpIntVal;
 
            try {
                iIn.skip(tmpIntVal);
            } catch (IOException e) {
                throw new com.sun.star.io.IOException(e.toString());
            }
        } while (tmpLongVal > 0);
    }
}