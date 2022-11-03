package com.miaogy.server.session;

import io.netty.channel.Channel;

public interface Session {
    // 会话
    void bind(Channel channel, String username);
    
    // 解绑会话
    void unbind(Channel channel);
    
    // 获取属性
    Object getAttribute(Channel channel, String name);
    
    // 设置属性
    void setAttribute(Channel channel, String name, Object value);
    
    // 有username获取channel
    Channel getChannel(String username);
    
}



















