package com.taofang.jsonrpc.exception;

public class JsonRpcWrongArugmentException extends JsonRpcException {

    
    public JsonRpcWrongArugmentException(){
        super();
    }
    
    public JsonRpcWrongArugmentException(String message){
        super(message);
    }
    
    public JsonRpcWrongArugmentException(String message,Throwable e){
        
        super(message,e);
    }
}
