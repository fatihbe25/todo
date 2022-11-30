package com.it4us.todoapp.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @GetMapping("/")
    public String Hello() throws UnknownHostException{

        var hostname=InetAddress.getLocalHost().getHostName();

        return  MessageFormat.format("It works!. ** Spring boot application *** {0} ", hostname);
    }
}



 