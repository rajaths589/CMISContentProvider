/**
 * ************************************************************
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 ************************************************************
 */
package org.apache.aoo.cmisucp.unobojects;

import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.ucb.CommandInfo;
import com.sun.star.ucb.GlobalTransferCommandArgument;
import com.sun.star.uno.Type;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rajath
 */
public final class CMISCommandInfo extends WeakBase
        implements com.sun.star.ucb.XCommandInfo {

    private final XComponentContext m_xContext;
    private static final String m_implementationName = CMISCommandInfo.class.getName();
    public static List<CommandInfo> supported_commands;

    /**
     *
     * @param context
     */
    public CMISCommandInfo(XComponentContext context) {
        m_xContext = context;
        supported_commands = new ArrayList<CommandInfo>();

        addCommands();
    }

    ;

    //My method
    private void addCommands() {
        supported_commands.add(new CommandInfo("getCommandInfo", -1, Type.VOID));
        supported_commands.add(new CommandInfo("getPropertySetInfo", -1, Type.VOID));
        supported_commands.add(new CommandInfo("getPropertyValues", -1, Type.ANY));
        supported_commands.add(new CommandInfo("setPropertyValues", -1, Type.ANY));
        supported_commands.add(new CommandInfo("open", -1, Type.ANY));
        supported_commands.add(new CommandInfo("update", -1, Type.ANY));
        supported_commands.add(new CommandInfo("synchronize", -1, Type.ANY));
        supported_commands.add(new CommandInfo("close", -1, Type.VOID));
        supported_commands.add(new CommandInfo("delete", -1, Type.BOOLEAN));
        supported_commands.add(new CommandInfo("insert", -1, Type.ANY));
        supported_commands.add(new CommandInfo("transfer", -1, Type.ANY));        
        supported_commands.add(new CommandInfo("createNewContent", -1, Type.ANY));
        supported_commands.add(new CommandInfo("CreatableContentsInfo", -1, Type.ANY));       
    }

    // com.sun.star.ucb.XCommandInfo:
    public com.sun.star.ucb.CommandInfo[] getCommands() {
        return (CommandInfo[]) supported_commands.toArray();
    }

    /**
     *
     * @param Name
     * @return
     * @throws com.sun.star.ucb.UnsupportedCommandException
     */
    public com.sun.star.ucb.CommandInfo getCommandInfoByName(String Name) throws com.sun.star.ucb.UnsupportedCommandException {
        for (CommandInfo c : supported_commands) {
            if (c.Name.equalsIgnoreCase(Name)) {
                return c;
            }
        }

        return new com.sun.star.ucb.CommandInfo();
    }

    /**
     *
     * @param Handle
     * @return
     * @throws com.sun.star.ucb.UnsupportedCommandException
     */
    public com.sun.star.ucb.CommandInfo getCommandInfoByHandle(int Handle) throws com.sun.star.ucb.UnsupportedCommandException {
        // TODO: Exchange the default return implementation for "getCommandInfoByHandle" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return new com.sun.star.ucb.CommandInfo();
    }

    /**
     *
     * @param Name
     * @return
     */
    public boolean hasCommandByName(String Name) {
        for (CommandInfo c : supported_commands) {
            if (c.Name.equalsIgnoreCase(Name)) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     * @param Handle
     * @return
     */
    public boolean hasCommandByHandle(int Handle) {
        // TODO: Exchange the default return implementation for "hasCommandByHandle" !!!
        // NOTE: Default initialized polymorphic structs can cause problems
        // because of missing default initialization of primitive types of
        // some C++ compilers or different Any initialization in Java and C++
        // polymorphic structs.
        return false;
    }
}
