package com.miaogy.server.session;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Set;

public interface GroupSession {
    // 创建群聊
    Group createGroup(String name, Set<String> members);
    
    // 加入群聊
    Group joinMember(String name, String member);
    
    // 移除成员
    Group removeMember(String name, String member);
    
    // 删除群聊
    Group removeGroup(String name);
    
    // 获取组员
    Set<String> getMembers(String name);
    
    // 获取在线组员的channel
    List<Channel> getMembersChannel(String name);
    
    
}










