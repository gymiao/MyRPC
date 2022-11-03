package com.miaogy.server.session;

// 抽象类
public abstract class GroupSessionFactory {
    private static GroupSession session = new GroupSessionMemoryImpl();
    
    public static GroupSession getGroupSession() {
        return session;
    }

}
