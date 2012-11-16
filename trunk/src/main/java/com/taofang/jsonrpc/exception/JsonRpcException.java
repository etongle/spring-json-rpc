package com.taofang.jsonrpc.exception;

public class JsonRpcException extends RuntimeException {

    
    public JsonRpcException(){
        super();
    }
    
    public JsonRpcException(String message){
        super(message);
    }
    
    public JsonRpcException(String message,Throwable e){
        super(message,e);
    }
}
