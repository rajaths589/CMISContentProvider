package org.apache.aoo.cmisucp.dialog;

import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;


public final class OptionsDialogHandler extends WeakBase
   implements com.sun.star.awt.XContainerWindowEventHandler
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = OptionsDialogHandler.class.getName();

    public OptionsDialogHandler( XComponentContext context )
    {
        m_xContext = context;
    };

    // com.sun.star.awt.XContainerWindowEventHandler:
    public boolean callHandlerMethod(com.sun.star.awt.XWindow xWindow, Object EventObject, String MethodName) throws com.sun.star.lang.WrappedTargetException
    {
        // TODO: Exchange the default return implementation for "callHandlerMethod" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

    public String[] getSupportedMethodNames()
    {
        // TODO: Exchange the default return implementation for "getSupportedMethodNames" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new String[] { "external_event" };
    }

}
