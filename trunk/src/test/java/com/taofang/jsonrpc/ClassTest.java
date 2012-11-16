package com.taofang.jsonrpc;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Future;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.scheduling.annotation.AsyncResult;

import com.caucho.hessian.io.Hessian2Output;
import com.taofang.jsonrpc.service.impl.Person;

public class ClassTest {

    public static void main(String[] args) throws Exception{
        
       ObjectMapper objectMapper = new ObjectMapper();
       NullPointerException nullPoint = new NullPointerException("Just Test");
       String content = objectMapper.writeValueAsString(nullPoint);
       System.out.println(content);
       objectMapper.getDeserializationConfig().disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
       System.out.println(JsonRpcExporter.class.getName());
       System.out.println(objectMapper.writeValueAsString(new Integer(10)));
       System.out.println(objectMapper.readValue("10", Integer.class) + 2);
       ByteArrayOutputStream bos = new ByteArrayOutputStream();
       bos.write("5".getBytes());
       System.out.println(bos.toByteArray()[0]);
       AsyncResult<String> ar = new AsyncResult<String>("Hello");
      
       System.out.println(ar instanceof Future);
       System.out.println(System.currentTimeMillis());
       System.out.println("1".getBytes().length);
       System.out.println("11".getBytes().length);
       System.out.println("11367".getBytes().length);
       System.out.println("ab".getBytes().length);
      // throw objectMapper.readValue(content, NullPointerException.class);
       testJdkSeri();
    }
    
    public static void testJdkSeri() throws Exception {
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        Person p = new Person();
        p.setAge(2);
        p.setName("zhangsan");
        os.writeObject(p);
        System.out.println(bos.toByteArray().length);
       System.out.println(new String(bos.toByteArray())); 
       ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
       Hessian2Output hop = new Hessian2Output(bos1);
       hop.writeObject(p);
       hop.flush();
       System.out.println(bos1.toByteArray().length);
       System.out.println(new String(bos1.toByteArray())); 
    }
    
}
