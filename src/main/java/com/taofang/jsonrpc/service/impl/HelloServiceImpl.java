package com.taofang.jsonrpc.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.taofang.jsonrpc.scanner.JsonRpc;
import com.taofang.jsonrpc.service.IHelloService;

@Component("helloService")
@JsonRpc("helloService")
public class HelloServiceImpl implements IHelloService {

    public Integer add(Integer a, Integer b) {
       
        return a + b;
    }

    public Person person() {
     
        Person p = new Person();
        p.setAge(23);
        p.setName("zhangsan");
        return p;
    }

    public String test(String a, String c) {
        return a + c + 1 / 0;
    }

    public List<Integer> list(int size) {
        List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(4);
        list.add(5);
        return list;
    }
    
    

}
