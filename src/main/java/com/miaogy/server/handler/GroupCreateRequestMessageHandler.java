package com.miaogy.server.handler;

import com.miaogy.message.GroupCreateRequestMessage;
import com.miaogy.message.GroupCreateResponseMessage;
import com.miaogy.server.session.Group;
import com.miaogy.server.session.GroupSession;
import com.miaogy.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        // 群管理
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(groupName, members);
        if (group == null) {
            List<Channel> channels = groupSession.getMembersChannel(groupName);
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));
            for (Channel channel : channels) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "你已被拉入"+groupName));
            }
        }else {
            ctx.writeAndFlush(new GroupCreateResponseMessage( false, "创建失败，群名已存在"));
        }
    
    }
}























