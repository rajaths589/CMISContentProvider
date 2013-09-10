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
package org.apache.aoo.cmisucp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.chemistry.opencmis.commons.PropertyIds;

/**
 *
 * @author rajath
 */
public class CMISConstants {

    /**
     *
     */
    public static final Map<String, String> propertiesHashMap;

    static {
        Map<String, String> tempMap = new HashMap<String, String>();

        tempMap.put("Title", PropertyIds.NAME);
        tempMap.put("IsFolder", PropertyIds.BASE_TYPE_ID);
        tempMap.put("IsDocument", PropertyIds.BASE_TYPE_ID);
        tempMap.put("DateCreated", PropertyIds.CREATION_DATE);
        tempMap.put("DateModified", PropertyIds.LAST_MODIFICATION_DATE);
        tempMap.put("Size", PropertyIds.CONTENT_STREAM_LENGTH);
        tempMap.put("MediaType", PropertyIds.CONTENT_STREAM_MIME_TYPE);
        tempMap.put("ContentType", PropertyIds.CONTENT_STREAM_MIME_TYPE);

        propertiesHashMap = Collections.unmodifiableMap(tempMap);
    }
}
