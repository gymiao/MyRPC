package com.miaogy.server.session;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionMemoryImpl implements Session{
    
    // 保存用户的channel关系
    private final Map<String, Channel> usernameChannelMap = new ConcurrentHashMap<>();
    
    // 保存channel和用户关系
    private final Map<Channel, String> channelUsernameMap = new ConcurrentHashMap<>();
    
    // 保存channel的属性相关内容
    private final Map<Channel, Map<String, Object>> channelAttributesMap = new ConcurrentHashMap<>();
    
    
    @Override
    public void bind(Channel channel, String username) {
        usernameChannelMap.put(username, channel);
        channelUsernameMap.put(channel, username);
        // 刚开始没有相关属性，赋值为空而不是null
        channelAttributesMap.put(channel, new ConcurrentHashMap<>());
        
    }
    
    @Override
    public void unbind(Channel channel) {
        String username = channelUsernameMap.remove(channel);
        usernameChannelMap.remove(username);
        channelAttributesMap.remove(channel);
    }
    
    @Override
    public Object getAttribute(Channel channel, String name) {
        // 一个channel允许有好几个username
        return channelAttributesMap.get(channel).get(name);
    }
    
    @Override
    public void setAttribute(Channel channel, String name, Object value) {
        // channelAttributesMap是一个<Channel, Map<>>类型集合，需要进行相应的处理
        channelAttributesMap.get(channel).put(name,value);
    }
    
    @Override
    public Channel getChannel(String username) {
        return usernameChannelMap.get(username);
    }
    
    @Override
    public String toString() {
        return usernameChannelMap.toString();
    }
}

















