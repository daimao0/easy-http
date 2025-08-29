package com.cy.easyhttp;

import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置类
 *
 * @author cy
 * @since v1.0.0
 */
public class HttpClientConfiguration {
    /**
     * 基础 URL
     */
    private final String baseUrl;
    /**
     * 请求头
     */
    private final Map<String, String> headers;
    /**
     * okHttpClient
     */
    private final OkHttpClient okHttpClient;

    // 私有构造函数，只能通过Builder创建实例
    private HttpClientConfiguration(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.headers = builder.headers;
        this.okHttpClient = builder.okHttpClient;
    }

    // 静态方法：创建Builder实例（更符合常见用法）
    public static Builder newBuilder() {
        return new Builder();
    }

    // Getter方法
    public String getBaseUrl() {
        return baseUrl;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    // Builder类
    public static class Builder {
        private String baseUrl;
        private Map<String, String> headers;
        private OkHttpClient okHttpClient;

        // Builder构造函数设为私有，强制通过外层的newBuilder()创建
        private Builder() {
            this.headers = new HashMap<>();
        }

        // 设置baseUrl并返回Builder
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        // 设置headers并返回Builder
        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        // 添加单个请求头
        public Builder addHeader(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        // 设置okHttpClient并返回Builder
        public Builder okHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        // 构建Configuration实例
        public HttpClientConfiguration build() {
            return new HttpClientConfiguration(this);
        }
    }
}