package com.taofang.jsonrpc.client;

public interface ServiceProxyFactory {

    /**
     * Creates a new proxy with the specified URL.  The returned object
     * is a proxy with the interface specified by api.
     *
     * <pre>
     * String url = "http://localhost:8080/ejb/hello");
     * HelloHome hello = (HelloHome) factory.create(HelloHome.class, url);
     * </pre>
     *
     * @param api the interface the proxy class needs to implement
     * @param url the URL where the client object is located.
     *
     * @return a proxy to the object with the specified interface.
     */
    public Object create(Class api, String url)
      throws java.net.MalformedURLException;
}
