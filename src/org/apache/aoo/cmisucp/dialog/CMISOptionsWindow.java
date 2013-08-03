package org.apache.aoo.cmisucp.dialog;

import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.ComponentBase;


public final class CMISOptionsWindow extends ComponentBase
   implements com.sun.star.awt.XWindow
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISOptionsWindow.class.getName();

    public CMISOptionsWindow( XComponentContext context )
    {
        m_xContext = context;
    };

    // com.sun.star.awt.XWindow:
    public void setPosSize(int X, int Y, int Width, int Height, short Flags)
    {
        // TODO: Insert your implementation for "setPosSize" here.
    }

    public com.sun.star.awt.Rectangle getPosSize()
    {
        // TODO: Exchange the default return implementation for "getPosSize" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new com.sun.star.awt.Rectangle();
    }

    public void setVisible(boolean Visible)
    {
        // TODO: Insert your implementation for "setVisible" here.
    }

    public void setEnable(boolean Enable)
    {
        // TODO: Insert your implementation for "setEnable" here.
    }

    public void setFocus()
    {
        // TODO: Insert your implementation for "setFocus" here.
    }

    public void addWindowListener(com.sun.star.awt.XWindowListener xListener)
    {
        // TODO: Insert your implementation for "addWindowListener" here.
    }

    public void removeWindowListener(com.sun.star.awt.XWindowListener xListener)
    {
        // TODO: Insert your implementation for "removeWindowListener" here.
    }

    public void addFocusListener(com.sun.star.awt.XFocusListener xListener)
    {
        // TODO: Insert your implementation for "addFocusListener" here.
    }

    public void removeFocusListener(com.sun.star.awt.XFocusListener xListener)
    {
        // TODO: Insert your implementation for "removeFocusListener" here.
    }

    public void addKeyListener(com.sun.star.awt.XKeyListener xListener)
    {
        // TODO: Insert your implementation for "addKeyListener" here.
    }

    public void removeKeyListener(com.sun.star.awt.XKeyListener xListener)
    {
        // TODO: Insert your implementation for "removeKeyListener" here.
    }

    public void addMouseListener(com.sun.star.awt.XMouseListener xListener)
    {
        // TODO: Insert your implementation for "addMouseListener" here.
    }

    public void removeMouseListener(com.sun.star.awt.XMouseListener xListener)
    {
        // TODO: Insert your implementation for "removeMouseListener" here.
    }

    public void addMouseMotionListener(com.sun.star.awt.XMouseMotionListener xListener)
    {
        // TODO: Insert your implementation for "addMouseMotionListener" here.
    }

    public void removeMouseMotionListener(com.sun.star.awt.XMouseMotionListener xListener)
    {
        // TODO: Insert your implementation for "removeMouseMotionListener" here.
    }

    public void addPaintListener(com.sun.star.awt.XPaintListener xListener)
    {
        // TODO: Insert your implementation for "addPaintListener" here.
    }

    public void removePaintListener(com.sun.star.awt.XPaintListener xListener)
    {
        // TODO: Insert your implementation for "removePaintListener" here.
    }

}
