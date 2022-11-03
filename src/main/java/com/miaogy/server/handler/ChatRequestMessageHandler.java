package com.miaogy.server.handler;

import com.miaogy.message.ChatRequestMessage;
import com.miaogy.message.ChatResponseMessage;
import com.miaogy.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo(); // 发给谁
        // 获取消息接收方的channel
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel != null) {
            // 发送消息发送者与消息内容
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
        
        else {
            // 告知发送者，接收者不在线
            ctx.writeAndFlush(new ChatResponseMessage(false,"接收者不在线"));
        }
    }
}














