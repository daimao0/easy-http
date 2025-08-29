package com.cy.easyhttp;

import java.lang.reflect.Proxy;

/**
 * HTTP客户端代理工厂，用于创建接口的实现类
 *
 * @author cy
 * @since v1.0.0
 */
public class HttpClientProxyFactory {


    /**
     * 创建代理实现类
     *
     * @param clazz EasyHttp定义的接口类
     * @param <T>   接口类型
     * @return 代理实现类
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz) {
        // 检查接口是否被@HttpClient注解标记
        if (!clazz.isInterface() || !clazz.isAnnotationPresent(HttpClient.class)) {
            throw new IllegalArgumentException("只支持@HttpClient定义的接口");
        }
        // 创建动态代理
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new HttpClientInvocationHandler(clazz)
        );
    }


    /**
     * 创建代理实现类
     *
     * @param clazz         EasyHttp定义的接口类
     * @param <T>           接口类型
     * @param configuration 客户端配置项
     * @return 代理实现类
     */
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> clazz, HttpClientConfiguration configuration) {
        // 检查接口是否被@HttpClient注解标记
        if (!clazz.isInterface() || !clazz.isAnnotationPresent(HttpClient.class)) {
            throw new IllegalArgumentException("只支持@HttpClient定义的接口");
        }
        // 创建动态代理
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                new HttpClientInvocationHandler(clazz, configuration)
        );
    }
}
