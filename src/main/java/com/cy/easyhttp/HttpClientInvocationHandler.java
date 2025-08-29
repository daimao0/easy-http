package com.cy.easyhttp;

import com.cy.easyhttp.method.Delete;
import com.cy.easyhttp.method.Get;
import com.cy.easyhttp.method.Post;
import com.cy.easyhttp.method.Put;
import okhttp3.Request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Http客户端调用处理器，实现动态代理的逻辑
 *
 * @author Cyan
 * @since v1.0.0
 */
public class HttpClientInvocationHandler implements InvocationHandler {

    private final String baseUrl;
    private final Map<String, String> defaultHeaders;

    public HttpClientInvocationHandler(Class<?> clazz) {
        HttpClient annotation = clazz.getAnnotation(HttpClient.class);
        // 基础url
        this.baseUrl = annotation.baseUrl();
        this.defaultHeaders = parseHeaders(annotation.headers());
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        //解析方法上的HTTP注解
        //构建请求
        Request request = buildRequest(method);
        return null;
    }

    /**
     * 构建请求
     *
     * @param method 方法
     * @return http请求
     * @since v1.0.0
     */
    private Request buildRequest(Method method) {
        //请求方法
        Request.Builder builder = new Request.Builder();
        //路径
        String path = buildUrl(method);
        builder.url(path);
        //设置请求头
        setHeaders(builder, method);
        Map<String, String> headers = new HashMap<>();
       return null;
    }

    /**
     * 构建完整URL
     */
    private String buildUrl(Method method) {
        String path = "";

        if (method.isAnnotationPresent(Get.class)) {
            path = method.getAnnotation(Get.class).value();
        } else if (method.isAnnotationPresent(Post.class)) {
            path = method.getAnnotation(Post.class).value();
        }

        // 拼接baseUrl和path，处理斜杠问题
        return baseUrl + (path.startsWith("/") ? "" : "/") + path;
    }

    /**
     * 设置请求头
     */
    private void setHeaders(Request.Builder requestBuilder, Method method) {
        // 添加默认请求头
        defaultHeaders.forEach(requestBuilder::addHeader);

        // 添加方法级别的请求头
        Map<String, String> methodHeaders = new HashMap<>();

        if (method.isAnnotationPresent(Get.class)) {
            methodHeaders.putAll(parseHeaders(method.getAnnotation(Get.class).headers()));
        } else if (method.isAnnotationPresent(Post.class)) {
            methodHeaders.putAll(parseHeaders(method.getAnnotation(Post.class).headers()));
        }

        methodHeaders.forEach(requestBuilder::addHeader);
    }

    /**
     * 解析请求头数组为Map
     *
     * @param headers 请求头数组
     * @since v1.0.0
     */
    private Map<String, String> parseHeaders(String[] headers) {
        Map<String, String> headerMap = new HashMap<>();
        for (String header : headers) {
            String[] parts = header.split(":", 2);
            if (parts.length == 2) {
                headerMap.put(parts[0].trim(), parts[1].trim());
            }
        }
        return headerMap;
    }
}
