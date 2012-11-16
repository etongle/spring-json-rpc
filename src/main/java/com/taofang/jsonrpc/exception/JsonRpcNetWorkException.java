package com.taofang.jsonrpc.exception;

public class JsonRpcNetWorkException extends JsonRpcException {

    
    public JsonRpcNetWorkException(){
        super();
    }
    
    public JsonRpcNetWorkException(String message){
        super(message);
    }
    
    public JsonRpcNetWorkException(String message,Throwable e){
        
        super(message,e);
    }
}
