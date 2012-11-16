package com.taofang.jsonrpc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    
    @RequestMapping("/say")
    public @ResponseBody String say(Integer age){
        
        return "you age is" + age;
    }
}
