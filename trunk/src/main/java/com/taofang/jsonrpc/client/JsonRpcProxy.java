package com.taofang.jsonrpc.client;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.caucho.hessian.client.HessianProxy;
import com.taofang.jsonrpc.exception.JsonRpcNetWorkException;

public class JsonRpcProxy implements InvocationHandler {

    protected JsonRpcProxyFactory factory;


    private Class<?> type;
    private String url;

    public JsonRpcProxy(Class<?> type, String url, JsonRpcProxyFactory factory) {

        this.type = type;
        this.url = url;
        this.factory = factory;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String methodName = method.getName();
        Class<?>[] params = method.getParameterTypes();
        // equals and hashCode are special cased
        if (methodName.equals("equals")
                && params.length == 1 && params[0].equals(Object.class)) {
            Object value = args[0];
            if (value == null || !Proxy.isProxyClass(value.getClass()))
                return Boolean.FALSE;

            Object proxyHandler = Proxy.getInvocationHandler(value);

            if (!(proxyHandler instanceof HessianProxy))
                return Boolean.FALSE;

            JsonRpcProxy handler = (JsonRpcProxy) proxyHandler;

            return new Boolean(url.equals(handler.getUrl()));
        }
        else if (methodName.equals("hashCode") && params.length == 0)
            return new Integer(url.hashCode());
        
        JsonRpcConnection conn = null;
        
        return null;

    }
    
    protected void sendRequest(String methodName,Object[] args) {
        

        URL uRL;
        try {
            uRL = new URL(url + "?method=" + methodName);
           JsonRpcConnection conn =  factory.getConnFactory().open(uRL);
           if(args != null){
               Map<String,String> argsMap = new HashMap<String,String>(args.length);
               for(Object obj:args){
                   
               }
           }
           //Map<String>
           //factory.getObjectMapper().writeValueAsString(value)
           //conn.w
        } catch (MalformedURLException e) {
            throw new JsonRpcNetWorkException(e.getMessage(), e);
        } catch (IOException e) {
            throw new JsonRpcNetWorkException(e.getMessage(), e);
        }
        
        
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    

}
