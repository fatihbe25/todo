package com.it4us.todoapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class DefaultController {
    
    @GetMapping("/")
    public String defaultString(){
        return "It works. *** Spring boot Rest Api *** ";
    }
}
