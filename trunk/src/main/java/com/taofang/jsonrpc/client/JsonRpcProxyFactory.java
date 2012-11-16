package com.taofang.jsonrpc.client;

import java.net.MalformedURLException;

import org.codehaus.jackson.map.ObjectMapper;

import com.taofang.jsonrpc.exception.JsonRpcNetWorkException;

public class JsonRpcProxyFactory implements ServiceProxyFactory {

    private ObjectMapper objectMapper = new ObjectMapper();
    private JsonRpcConnectionFactory connFactory = new JsonRpcUrlConnecitonFactory();
    
    public JsonRpcConnectionFactory getConnFactory() {
        return connFactory;
    }

    public void setConnFactory(JsonRpcConnectionFactory connFactory) {
        this.connFactory = connFactory;
    }

    private int readTimeout = -1;
    private int connectTimeout = -1;

    public Object create(Class api, String url) throws MalformedURLException {
        return null;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    
}
