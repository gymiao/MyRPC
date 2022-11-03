package com.miaogy.server.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GroupSessionMemoryImpl implements GroupSession{
    // ConcurrentHashMap是一个支持高并发更新与查询的哈希表
    private final Map<String, Group> groupMap = new ConcurrentHashMap<>();
    @Override
    public Group createGroup(String name, Set<String> members) {
        // 创建一个群聊
        Group group = new Group(name, members);
        // 将群聊加入groupMap中方便管理
        return groupMap.putIfAbsent(name, group);
    }
    
    @Override
    public Group joinMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key,value) -> {
            // 此处value为group, group.getMembers()得到成员的set<string>集合，将member加入set集合中
            value.getMembers().add(member);
            return value;
        });
    }
    
    @Override
    public Group removeMember(String name, String member) {
        return groupMap.computeIfPresent(name, (key,value)->{
            value.getMembers().remove(member);
            return value;
        });
    }
    
    @Override
    public Group removeGroup(String name) {
        return groupMap.remove(name);
    }
    
    @Override
    public Set<String> getMembers(String name) {
        // 显然先要得到group，然后再根据group得到members
        // getOrDefault函数意义：如果根据name得到的值为空，那么返回值就采用第二个默认的参数
        return groupMap.getOrDefault(name, Group.EMPTY_GROUP).getMembers();
    }
    
    @Override
    public List<Channel> getMembersChannel(String name) {
        return getMembers(name).stream()
                
                // session只会记录在线的channel, session.getChannel可以获得对应的channel
                .map(member -> SessionFactory.getSession().getChannel(member))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

















