package com.miaogy.message;

public class PongMessage extends Message{
    @Override
    public int getMessageType() {
        return PongMessage;
    }
}
