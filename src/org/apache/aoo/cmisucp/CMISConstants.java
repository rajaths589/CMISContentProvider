/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    public static final Map<String,String> propertiesHashMap;
    static
    {
        Map<String,String> tempMap = new HashMap<String, String>();
        
        tempMap.put("Title",PropertyIds.NAME);
        tempMap.put("IsFolder",PropertyIds.BASE_TYPE_ID);
        tempMap.put("IsDocument", PropertyIds.BASE_TYPE_ID);
        tempMap.put("DateCreated", PropertyIds.CREATION_DATE);
        tempMap.put("DateModified", PropertyIds.LAST_MODIFICATION_DATE);
        tempMap.put("Size", PropertyIds.CONTENT_STREAM_LENGTH);
        tempMap.put("MediaType", PropertyIds.CONTENT_STREAM_MIME_TYPE);
        tempMap.put("ContentType", PropertyIds.CONTENT_STREAM_MIME_TYPE);
        
        propertiesHashMap = Collections.unmodifiableMap(tempMap);
    }
}
