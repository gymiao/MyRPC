package com.miaogy.server.service;

public class HelloServiceImpl implements HelloService{
    @Override
    public String sayHello(String msg) {
        return "hello" + msg;
    }
}
