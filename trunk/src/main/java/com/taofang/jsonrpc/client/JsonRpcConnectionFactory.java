package com.taofang.jsonrpc.client;

import java.io.IOException;
import java.net.URL;

public interface JsonRpcConnectionFactory {

    public void setJsonRpcProxyFactory(JsonRpcProxyFactory proxyFactory);
    
    public JsonRpcConnection open(URL url) throws IOException;
}
