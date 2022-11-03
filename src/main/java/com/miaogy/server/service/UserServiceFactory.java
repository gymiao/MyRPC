package com.miaogy.server.service;

public class UserServiceFactory {
    private static UserService userService = new UserServiceMemoryImpl();
    
    // 提供一个服务
    public static UserService getUserService() {
        return userService;
    }
    
}
