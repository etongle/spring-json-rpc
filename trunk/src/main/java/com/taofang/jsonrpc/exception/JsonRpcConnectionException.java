package com.taofang.jsonrpc.exception;

public class JsonRpcConnectionException extends JsonRpcException {

    
    public JsonRpcConnectionException(){
        super();
    }
    
    public JsonRpcConnectionException(String message){
        super(message);
    }
    
    public JsonRpcConnectionException(String message,Throwable e){
        super(message,e);
    }
}
