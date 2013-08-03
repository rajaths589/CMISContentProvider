package org.apache.aoo.cmisucp.helper;

import org.apache.aoo.cmisucp.cmis.CMISConnect;

public class CMISNameHelper {
    
    private String requestedURL;
    private String serverUrl;
    private String parentURL;
    private String parentName;
    private String name;
    
    private final String http = "cmis";
    private final String https = "cmiss";
    
    public String getServerURL()
    {
        return serverUrl;
    }
    
    public String getParentName()
    {
        return parentName;        
    }
    
    public String getParentURL()
    {
        return parentURL;
    }
    
    public String getName()
    {
        return name;
    }
    
    private void splitURL(String uName, String pswd)
    {
        
    }
    
    public void setURL(String url)
    {
        requestedURL = url;
    }
}
