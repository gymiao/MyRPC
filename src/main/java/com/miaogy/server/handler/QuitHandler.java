package com.miaogy.server.handler;

import com.miaogy.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {
    // 连接断开
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 移除channel
        SessionFactory.getSession().unbind(ctx.channel());
        log.debug(ctx.channel()+"已断开");
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (ctx.channel() != null) {
            SessionFactory.getSession().unbind(ctx.channel());
        }
        log.debug(ctx.channel() + "出现异常，已经断开");
    }
}
