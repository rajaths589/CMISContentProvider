package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.ucb.CommandInfo;
import com.sun.star.uno.Type;
import java.util.ArrayList;
import java.util.List;


public final class CMISCommandInfo extends WeakBase
   implements com.sun.star.ucb.XCommandInfo
{
    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISCommandInfo.class.getName();
    public static List<CommandInfo> supported_commands;
    
    public CMISCommandInfo( XComponentContext context )
    {
        m_xContext = context;
        supported_commands = new ArrayList<CommandInfo>();
        
        addCommands();
    };

    //My method
    private void addCommands()
    {
        supported_commands.add(new CommandInfo("getCommandInfo", -1, Type.VOID));
        supported_commands.add(new CommandInfo("getPropertySetInfo", -1, Type.VOID));
        supported_commands.add(new CommandInfo("getPropertyValues", -1, Type.ANY));
        supported_commands.add(new CommandInfo("setPropertyValues", -1, Type.ANY));
        supported_commands.add(new CommandInfo("open",-1,Type.ANY));
        supported_commands.add(new CommandInfo("update",-1,Type.ANY));
        supported_commands.add(new CommandInfo("synchronize",-1,Type.ANY));
        supported_commands.add(new CommandInfo("close", -1, Type.VOID));
        supported_commands.add(new CommandInfo("delete",-1,Type.BOOLEAN));
        supported_commands.add(new CommandInfo("insert",-1,Type.ANY));
        supported_commands.add(new CommandInfo("search",-1,Type.ANY));
        supported_commands.add(new CommandInfo("transfer",-1,Type.ANY));
        supported_commands.add(new CommandInfo("createNewContent",-1,Type.ANY));
    }
    
    // com.sun.star.ucb.XCommandInfo:
    public com.sun.star.ucb.CommandInfo[] getCommands()
    {       
        return (CommandInfo[]) supported_commands.toArray();
    }

    public com.sun.star.ucb.CommandInfo getCommandInfoByName(String Name) throws com.sun.star.ucb.UnsupportedCommandException
    {
        for(CommandInfo c:supported_commands)
            if(c.Name.equalsIgnoreCase(Name))
                return c;
        
        return new com.sun.star.ucb.CommandInfo();
    }

    public com.sun.star.ucb.CommandInfo getCommandInfoByHandle(int Handle) throws com.sun.star.ucb.UnsupportedCommandException
    {
        // TODO: Exchange the default return implementation for "getCommandInfoByHandle" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new com.sun.star.ucb.CommandInfo();
    }

    public boolean hasCommandByName(String Name)
    {
        for(CommandInfo c:supported_commands)
            if(c.Name.equalsIgnoreCase(Name))
                return true;
        
        return false;
    }

    public boolean hasCommandByHandle(int Handle)
    {
        // TODO: Exchange the default return implementation for "hasCommandByHandle" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }

}
