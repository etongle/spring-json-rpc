package com.taofang.jsonrpc;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.ClassUtils;
import org.springframework.web.HttpRequestHandler;

import com.taofang.jsonrpc.exception.JsonRpcWrongArugmentException;
import com.taofang.jsonrpc.service.impl.HelloServiceImpl;


public class JsonRpcExporter extends RemoteExporter implements InitializingBean,HttpRequestHandler {

    private Map<String,Method> actualMethods;
    private Map<String,Method> beanMethods;
    private Map<String,Method> interfaceMethods;
    private String beanClassName;
    private static  ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    //默认参数
    private ObjectMapper objectMapper = new ObjectMapper();
    public void afterPropertiesSet() throws Exception {
          prepare();   
    }
    
    public void prepare() {
        checkService();
        checkServiceInterface();
        actualMethods = new HashMap<String, Method>();
        beanMethods = new HashMap<String,Method>();
        interfaceMethods = new HashMap<String,Method>();
        objectMapper.getDeserializationConfig().disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            Class beanClass = ClassUtils.forName(beanClassName,this.getBeanClassLoader());
            Method[] beanMs = beanClass.getDeclaredMethods();
            Method[] actualMs = getProxyForService().getClass().getDeclaredMethods();
            Method[] interfaceMs = getServiceInterface().getMethods();
            //合并循环
            
            for(Method item:beanMs){
                beanMethods.put(item.getName(), item);
            }
            for(Method item:actualMs){
                actualMethods.put(item.getName(), item);
            }
            for(Method item:interfaceMs){
                interfaceMethods.put(item.getName(), item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("初始化" + beanClassName + "时发生异常，请检查配置 ");
        } 
       
        
        //this.skeleton = new HessianSkeleton(getProxyForService(), getServiceInterface());
    }
    
    
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       
         try {
            invoke(request, response);
        } catch (Throwable e) {
            e.printStackTrace();
                try {
                    writeFaultToResponse(e, response);
                } catch (Throwable e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
           
            
        }
        
    }
    
    private void writeFaultToResponse(Throwable e,HttpServletResponse response) throws Throwable {
        Throwable actualThrowable = e;
        if(e instanceof InvocationTargetException){
            actualThrowable = e.getCause();
        }
        response.addHeader("exception", actualThrowable.getClass().getName());
        response.addHeader("success", "1");
        writeJsonToResponse(actualThrowable, response);
    }
    
    private void writeResultToResponse(Object result,HttpServletResponse response) throws Throwable{
        response.addHeader("success", "0");
        writeJsonToResponse(result, response);
    }

    public void invoke(HttpServletRequest request,HttpServletResponse response) throws Throwable {
        
        String method = request.getParameter("method");
        Method beanMethod = beanMethods.get(method);
        Method actualMethod = actualMethods.get(method);
        Method interfaceMethod = interfaceMethods.get(method);
        Object[] args = resolveHandlerArguments(beanMethod,interfaceMethod,request);
        Object result = actualMethod.invoke(getProxyForService(),args);
        writeResultToResponse(result,response);
        
    }
    
    private void writeJsonToResponse(Object result,HttpServletResponse response) throws IOException{
        
        Class cl = result.getClass();
        response.setContentType("application/json;charset=UTF-8");
        OutputStream os =  response.getOutputStream();
       
        if(cl.equals(Integer.class)){
            Integer tmp = (Integer)result;
            //我犯了错误不能二进制编码整数,只能把整数先转为字符串
//            os.write(tmp >> 24);
//            os.write(tmp >> 16);
//            os.write(tmp >> 8);
//            os.write(tmp);
            os.write(tmp.toString().getBytes());
        }else{
            objectMapper.writeValue(os, result);
        }
    }
    
    private Object[] resolveHandlerArguments(Method handlerMethod,Method interfaceMethod,HttpServletRequest request) throws Throwable {
        
        Type[] types = handlerMethod.getParameterTypes();
      
        Object[] args = new Object[types.length];
        
        for(int i = 0; i < types.length; i++){
        
            MethodParameter methodParam = new MethodParameter(handlerMethod, i);
            AnnotationMethodParam annotationMethodParam = new AnnotationMethodParam(interfaceMethod, i);
            methodParam.initParameterNameDiscovery(parameterNameDiscoverer);
            GenericTypeResolver.resolveParameterType(methodParam, getService().getClass());
            Param param = annotationMethodParam.getParameterAnnotation(Param.class);
            String paramName = null;
            if(param != null){
                paramName = param.value();
            }
            if(paramName == null){
                paramName = methodParam.getParameterName();
            }
            if(paramName == null)
                throw new JsonRpcWrongArugmentException("Please check param annotation or check whether  " +
                		"method param name equeals request param name ");
            String paramValue = request.getParameter(paramName);
            if(paramValue == null){
                args[i] = null;
                continue;
            }
                
            Class  cl = methodParam.getParameterType();
            Object obj = null;
            if(cl.equals(Integer.class)){
                obj = Integer.valueOf(paramValue);
            }else if(cl.equals(String.class)){
                obj = paramValue;
            }else{
                
                 TypeFactory  _typeFactory = TypeFactory.defaultInstance();
                 JavaType javaType = _typeFactory.constructType(methodParam.getGenericParameterType());
                 obj =  objectMapper.readValue(paramValue.getBytes(), javaType);
            }
            
            args[i] = obj;
        }
        
        return args;
    }
    

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public static void main(String[] args){
        
       Method[] ms =  HelloServiceImpl.class.getMethods();
      
       MethodParameter mp = new MethodParameter(ms[0], 0);
       mp.initParameterNameDiscovery(parameterNameDiscoverer);
       GenericTypeResolver.resolveParameterType(mp, HelloServiceImpl.class);
       System.out.println(mp.getParameterName());
    }
    
    
    

    
}
