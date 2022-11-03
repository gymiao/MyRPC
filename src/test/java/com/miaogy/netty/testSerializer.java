package com.miaogy.netty;

import com.miaogy.message.LoginRequestMessage;
import com.miaogy.protocol.MessageCodeSharable;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

public class testSerializer {
    public static void main(String[] args) {
        MessageCodeSharable CODEC = new MessageCodeSharable();
        LoggingHandler LOGGING = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(LOGGING, CODEC, LOGGING);
        LoginRequestMessage msg = new LoginRequestMessage("zhangsan", "123");
       // channel.writeOutbound(msg);
    
    }
}

























