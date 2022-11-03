package com.miaogy.client;

import com.miaogy.message.*;
import com.miaogy.protocol.MessageCodeSharable;
import com.miaogy.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodeSharable MESSAGE_CODEC = new MessageCodeSharable();
         /*
        倒计时锁，CountDownLatch变为0时，程序才可以继续往下运行。
        目的是为了实现在登录之前，不进行其他的IO操作
        */
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));
                    // 对读写都需要进行操作
                    ch.pipeline().addLast(new ChannelDuplexHandler() {
                        // 用来出发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event = (IdleStateEvent) evt;
                            if (event.state() == IdleState.WRITER_IDLE) {
                                System.out.println("已经3s没有读取数据了");
                                ctx.writeAndFlush(new PingMessage());
                            }
            
                            // super.userEventTriggered(ctx, evt);
                        }
                    });
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    //ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    // 登录操作是在链接建立之后，因此是在此添加操作
                    ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter(){
                        //接受服务器输入
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            // super.channelRead(ctx, msg);
                            log.debug("msg:{}",msg);
                            if (msg instanceof LoginResponseMessage) {
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()) {
                                    LOGIN.set(true);
                                }
                                // 允许用户继续输入
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }
    
                        // 在建立连接之后出发active事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            // 创建读写线程，不会影响当前线程
                            new Thread(()->{
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("输入用户名: ");
                                String username = scanner.nextLine();
                                System.out.println("输入密码: ");
                                String password = scanner.nextLine();
                                // 构造消息对象
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                // 发送消息
                                // 出栈操作，往上执行
                                ctx.writeAndFlush(message);
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                                if(!LOGIN.get()) {
                                    //
                                    System.out.println("登录失败，关闭channel连接");
                                    ctx.channel().close();
                                    return;
                                }
                                while(true) {
                                    // 选择后续操作
                                    System.out.println("=============================");
                                    System.out.println("send [username] [msg]");
                                    System.out.println("gsend [groupName] [msg]");
                                    System.out.println("gcreate [groupName] [m1,m2,m3...]");
                                    System.out.println("gmembers [groupName]");
                                    System.out.println("gjoin [groupName]");
                                    System.out.println("gquit [groupName]");
                                    System.out.println("quit");
                                    System.out.println("=============================");
                                    String command = scanner.nextLine();
                                    String[] s = command.split(" ");
                                    switch (s[0]) {
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gcreate":
                                            Set<String> set = new HashSet<>( Arrays.asList(s[2].split(",")));
                                            set.add(username);
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            break;
                                            
                                    }
    
                                }
                                
                            }, "system in").start();
                            
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8089).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }
}






































