package com.miaogy.protocol;

import com.miaogy.config.Config;
import com.miaogy.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
// 记录同时在线的数量

// 负责消息的编解码
public class MessageCodeSharable extends MessageToMessageCodec<ByteBuf, Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        // 编码
        // ctx负责不同channelHandler之间的数据传输，因此最终的数据也在channelHandler中
        ByteBuf out = ctx.alloc().buffer(); // 要发送的数据
        // 1. 4 字节魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1 字节的版本信息
        out.writeByte(Config.getSerializerAlgorithm().ordinal());
        // 3. 1 字节的序列化方式 jdk 0, json 1
        out.writeByte(1);
        // 4. 1 字节的指令类型
        out.writeByte(msg.getMessageType());
        // 5. 4 个字节
        out.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerType];
        // 确定消息类型
        Class<?> messageClass = Message.getMessageClass(messageType);
        Object message = algorithm.deserialize(messageClass,bytes);;
//        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
//        log.debug("{}", message);
        out.add(message);
    }
}

























