package com.miaogy.message;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class RpcRequestMessage extends Message{
    
    // 接口名称
    private String interfaceName;
    
    // 接口中的方法
    private String methodName;
    
    // 方法返回类型
    private Class<?> returnType;
    
    // 方法参数类型数组
    private Class[] parameterTypes;
    
    // 方法参数值数组
    private Object[] parameterValue;
    
    public RpcRequestMessage(int sequenceId ,String interfaceName, String methodName, Class<?> returnType, Class[] parameterTypes, Object[] parameterValue) {
        super.setSequenceId(sequenceId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValue = parameterValue;
    }
    

    
    
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}


















