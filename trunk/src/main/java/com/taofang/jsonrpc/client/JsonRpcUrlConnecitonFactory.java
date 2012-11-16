package com.taofang.jsonrpc.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class JsonRpcUrlConnecitonFactory implements JsonRpcConnectionFactory {

    private JsonRpcProxyFactory proxyFactory;

    public void setJsonRpcProxyFactory(JsonRpcProxyFactory proxyFactory) {

        this.proxyFactory = proxyFactory;
    }

    public JsonRpcConnection open(URL url) throws IOException {

        HttpURLConnection conn;
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setChunkedStreamingMode(4 * 1024);
        conn.setDoInput(true);
        int connectTimeout = proxyFactory.getConnectTimeout();

        if (connectTimeout >= 0)
            conn.setConnectTimeout(connectTimeout);

        int readTimeout = proxyFactory.getReadTimeout();

        if (readTimeout > 0) {
            try {
                conn.setReadTimeout(readTimeout);
            } catch (Throwable e) {
            }
        }

        return new JsonRpcUrlConnection(url, conn);
    }

}
