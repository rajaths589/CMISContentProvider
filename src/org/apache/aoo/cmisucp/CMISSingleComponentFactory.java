package org.apache.aoo.cmisucp;

import com.sun.star.lang.XInitialization;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.UnoRuntime;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class CMISSingleComponentFactory extends WeakBase
   implements com.sun.star.lang.XSingleComponentFactory, com.sun.star.lang.XServiceInfo
{
    private Object theInstance;
    private static final String m_implementationName = CMISSingleComponentFactory.class.getName();    
    private Class<?> aClass;
    private java.lang.reflect.Constructor m_Constructor;
    private final String m_sImplementationName;
    private final String[] m_sSupportedServices;

    private CMISSingleComponentFactory(String sImplementationName, String[] sSupportedServices ) 
    {
        Class params[] = new Class[]{XComponentContext.class};
        m_sImplementationName = sImplementationName;
        m_sSupportedServices = sSupportedServices;        
        //m_Context = context;        
        try{
        aClass = CMISContentProvider.class;
        m_Constructor = aClass.getConstructor(params);                
        } catch(Exception e){
            Logger.getLogger(m_implementationName).log(Level.SEVERE,"Exception Caught",e);
        }
    };
    
    private Object instantiate(XComponentContext context) throws com.sun.star.uno.Exception
    {
        try{
            if(m_Constructor!=null)
            {
                return m_Constructor.newInstance(new Object[]{context});
            }
            return aClass.newInstance();
        }
        catch(java.lang.reflect.InvocationTargetException ex)
        {
            Throwable targetException = ex.getTargetException();
            if(targetException instanceof java.lang.RuntimeException)
                throw (java.lang.RuntimeException)targetException;
            else if(targetException instanceof com.sun.star.uno.RuntimeException)
                throw (com.sun.star.uno.RuntimeException)targetException;
            else if(targetException instanceof com.sun.star.uno.Exception)
                throw (com.sun.star.uno.Exception)targetException;
            else
                throw new com.sun.star.uno.Exception(targetException.toString(), this);
        }
        catch(IllegalAccessException ex)
        {
            throw new com.sun.star.uno.RuntimeException(ex.toString(),this);
        }
        catch(InstantiationException ex)
        {
            throw new com.sun.star.uno.RuntimeException(ex.toString(), this);
            
        }
    }
    private Object instantiateAndInit( XComponentContext m_Context, Object[] arguments ) throws com.sun.star.uno.Exception
    {
        Object inst = instantiate(m_Context);
        XInitialization xInit = UnoRuntime.queryInterface(XInitialization.class, inst);
        if(xInit!=null)
            xInit.initialize(arguments);
        
        return inst;
    }
    // com.sun.star.lang.XSingleComponentFactory:
    public Object createInstanceWithContext(com.sun.star.uno.XComponentContext Context) throws com.sun.star.uno.Exception
    {
        synchronized(this)
        {
            if(theInstance==null)
                theInstance = instantiate(Context);
            return theInstance;
        }        
    }

    public Object createInstanceWithArgumentsAndContext(Object[] Arguments, com.sun.star.uno.XComponentContext Context) throws com.sun.star.uno.Exception
    {
        synchronized(this)
        {
            if(theInstance==null)
                theInstance = instantiateAndInit(Context, Arguments);
            
            return theInstance;
        }        
    }
   
    public static XSingleComponentFactory getComponentFactory(String impName, String[] suppServices)
    {
        Logger.getLogger(m_implementationName).log(Level.SEVERE, m_implementationName);
        return new CMISSingleComponentFactory(impName,suppServices);
    }

    public String getImplementationName() {
        return m_sImplementationName;
    }

    public boolean supportsService(String arg0) {
        for(String s:m_sSupportedServices)
        {
            if(s.equals(arg0))
                return true;
        }
        
           return false;
    }

    public String[] getSupportedServiceNames() {
        return m_sSupportedServices;
    }
}
