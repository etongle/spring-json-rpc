package com.taofang.jsonrpc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationMethodParam {

    private int index;
    private Method method;
    private Annotation[] annotations;
    
    public AnnotationMethodParam(Method method,int index){
        
        this.method = method;
        this.index = index;
        this.annotations = method.getParameterAnnotations()[index];
    }
    
    
    public <T extends Annotation> T getParameterAnnotation(Class<T> annotationType) {
     
        for (Annotation ann : annotations) {
            if (annotationType.isInstance(ann)) {
                return (T) ann;
            }
        }
        return null;
    }
    
}
