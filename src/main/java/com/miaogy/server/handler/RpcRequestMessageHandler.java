package com.miaogy.server.handler;

import com.miaogy.message.RpcRequestMessage;
import com.miaogy.message.RpcResponseMessage;
import com.miaogy.server.service.HelloService;
import com.miaogy.server.service.ServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
// 需要实现功能，接受客户端传过来的数据，调用相应函数进行处理并将处理结果返回
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        try {
            HelloService service = (HelloService)
                    ServiceFactory.getService(Class.forName(message.getInterfaceName()));
    
            // 根据方法名与相应的参数类型得到方法对象
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
    
            // 执行方法对象，获取结果
            Object invoke = method.invoke(service, message.getParameterValue());
            
            // 设置返回对象
            response.setReturnValue(invoke);
            // System.out.println(invoke);
        } catch ( Exception e ) {
            e.printStackTrace();
            response.setExceptionValue(e);
        }
        ctx.writeAndFlush(response);
    }
    
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "com.miaogy.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三"});
        
        // 根据接口名得到对象
        HelloService service = (HelloService) ServiceFactory.getService(Class.forName(message.getInterfaceName()));
        
        // 根据方法名与相应的参数类型得到方法对象
        Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
        
        // 执行方法对象，获取结果
        Object invoke = method.invoke(service, message.getParameterValue());
        System.out.println(invoke);
    
    
    }
    
}














