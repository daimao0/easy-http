package com.cy.easyhttp.util;

import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * HttpUtil简易okhttp工具类
 *
 * @author cy
 * @since v1.0.0
 */
public class HttpUtil {


    /**
     * OkHttpClient
     */
    private final OkHttpClient okHttpClient;

    public HttpUtil(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    /**
     * 通用GET请求
     *
     * @param path    请求路径
     * @param headers 请求头
     * @param params  查询参数
     * @param type    返回类型
     */
    public <T> T doGet(String path, Map<String, String> headers, Map<String, String> params, Type type) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(path)).newBuilder();

        // 添加查询参数
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .headers(Headers.of(headers))
                .build();

        return executeRequest(request, type);
    }


    /**
     * 通用POST请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @param type 返回类型
     * @param <T>  返回类型
     * @return 返回结果
     */
    public <T> T doPost(String path, Map<String, String> headers, Object body, Type type) {
        return doRequest(path, headers, body, type, "POST");
    }


    /**
     * 通用PUT请求
     *
     * @param path 请求路径
     * @param body 请求体
     * @param type 返回类型
     * @param <T>  返回类型
     * @return 返回结果
     */
    public <T> T doPut(String path, Map<String, String> headers, Object body, Type type) {
        return doRequest(path, headers, body, type, "PUT");
    }

    /**
     * 通用DELETE请求
     *
     * @param path   请求路径
     * @param params 查询参数
     * @param type   返回类型
     * @param <T>    返回类型
     * @return 返回结果
     */
    public <T> T doDelete(String path, Map<String, String> headers, Map<String, String> params, Type type) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(path)).newBuilder();
        // 添加查询参数
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .headers(Headers.of(headers))
                .delete()
                .build();
        return executeRequest(request, type);
    }

    /**
     * 通用请求处理
     *
     * @param path   请求路径
     * @param body   请求体
     * @param type   返回类型
     * @param method 请求方法
     */
    private <T> T doRequest(String path, Map<String, String> headers, Object body, Type type, String method) {
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(path));

        // 创建请求体
        RequestBody requestBody = null;

        if (body != null) {
            requestBody = new JsonRequestBody(EasySerializer.serialize(body), MediaType.parse("application/json; charset=utf-8"));
        }else {
            requestBody = new JsonRequestBody("", MediaType.parse("application/json; charset=utf-8"));
        }

        Request.Builder requestBuilder = new Request.Builder()
                .url(httpUrl)
                .method(method, requestBody)
                .headers(Headers.of(headers));
        return executeRequest(requestBuilder.build(), type);
    }


    /**
     *
     * 执行请求并处理响应
     *
     * @param request 请求
     * @param type    返回类型
     * @return 响应结果
     */
    private <T> T executeRequest(Request request, Type type) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String bodyStr = response.body() != null ? response.body().string() : "null";
                throw new IOException("okhttp HTTP Error: " + response.code() + ", Body: " + bodyStr);
            }

            if (response.body() == null) {
                throw new IOException("okhttp HTTP Error: " + response.code() + ", Body: null");
            }
            String body = response.body().string();
            return EasySerializer.deserialize(body, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
