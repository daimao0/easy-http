package com.cy.easyhttp;

import okhttp3.OkHttpClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * HTTP客户端代理工厂，用于创建接口的实现类
 *
 * @author cy
 * @since v1.0.0
 */
public class HttpClientProxyFactory {

    private static final OkHttpClient client = new OkHttpClient();

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {
        // 检查接口是否被@HttpClient注解标记
        if (!clazz.isInterface() || !clazz.isAnnotationPresent(HttpClient.class)) {
            throw new IllegalArgumentException("Only interfaces annotated with @HttpClient are supported");
        }


        // 创建动态代理
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new HttpClientInvocationHandler(clazz)
        );
    }
}
