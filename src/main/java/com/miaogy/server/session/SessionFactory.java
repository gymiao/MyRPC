package com.miaogy.server.session;

public abstract class SessionFactory {
    // 抽象工厂，单例模式定义一个session进行全局管理
    private static Session session = new SessionMemoryImpl();
    
    public static Session getSession() {
        return session;
    }
}
