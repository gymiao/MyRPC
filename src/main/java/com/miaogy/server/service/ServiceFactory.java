package com.miaogy.server.service;

import com.miaogy.config.Config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceFactory {
    // 配置文件
    static Properties properties;
    static Map<Class<?>, Object> map = new ConcurrentHashMap<>();
    
    static {
        
        // 读取配置文件
        try (InputStream in = Config.class.getResourceAsStream("/application.properties")){
            properties = new Properties();
            properties.load(in);
            Set<String> names = properties.stringPropertyNames();
            for (String name : names) {
                if (name.endsWith("Service")) {
                    // 反射获取Class,即类的类型
                    Class<?> interfaceClass = Class.forName(name);
    
                    // 获取实例，即service的实现类
                    Class<?> instanceClass = Class.forName(properties.getProperty(name));
                    map.put(interfaceClass, instanceClass.newInstance());
                }
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    // 作用由类名获取一个实例对象
    public static <T> T getService(Class<T> interfaceClass) {
        return (T) map.get(interfaceClass);
    }
    
}
