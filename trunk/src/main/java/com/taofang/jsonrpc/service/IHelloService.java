package com.taofang.jsonrpc.service;

import java.util.List;

import com.taofang.jsonrpc.service.impl.Person;

public interface IHelloService {

    
    public Integer  add(Integer a,Integer b);
    
    public String test(String a,String c);
    
    public List<Integer> list(int size);
    
    public Person person();
}
