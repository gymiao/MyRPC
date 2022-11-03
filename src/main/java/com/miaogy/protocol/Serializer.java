package com.miaogy.protocol;

import com.google.gson.*;
import com.miaogy.message.Message;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

public interface Serializer {
    // 反序列化，将字节流转化为对象,clazz表示对象类型
    <T> T deserialize(Class<T> clazz, byte[] bytes);
    
    // 序列化，将对象序列化为字节流
    <T> byte[] serialize(T object);
    
    enum Algorithm implements Serializer {
        Java {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return  (T) ois.readObject();
                } catch (IOException e) {
                    throw new RuntimeException("反序列化失败",e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败",e);
                }
    
            }
    
            @Override
            public <T> byte[] serialize(T object) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败",e);
                }
                return bos.toByteArray();
            }
        },
        Json {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = new String(bytes, StandardCharsets.UTF_8);
                return gson.fromJson(json,clazz);
            }
    
            @Override
            public <T> byte[] serialize(T object) {
                Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new Serializer.ClassCodec()).create();
                String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        }
    }
    
    class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
        
        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String str = json.getAsString();
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }
        
        @Override             //   String.class
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }
}














