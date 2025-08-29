# easy-http使用方法

本客户端基于okhttp3+jackson,实现将http请求定义为接口，动态代理发起请求，自动序列化请求参数，自动反序列化响应结果。

通过定义接口的方式方便开发人员提供模块给第三方使用。

适用场景：

    1 封装自己开发的http接口定义好返回值,给同事使用。
    2 封装第三方接口，给自己使用。
    3 适合不使用spring的项目或Feign接口不兼容的情况。(比如新项目是springboot3,但是旧项目是springboot2.x,可能会出现使用feign接口报错)

## 一、接口定义

![应用案例](/assert/swappy-20250829_164627.png)

## 二、客户端调用

```java
ApiService apiService = HttpClientProxyFactory.create(ApiService.class);
apiService.ping();
```

```java
//带参数的定义
HttpClientConfiguration configuration = HttpClientConfiguration.newBuilder()
                .baseUrl("http://localhost:8080")
                .headers(new HashMap<>())
                .addHeader("token", "123")
                .okHttpClient(new OkHttpClient())
                .build();
ApiService apiService = HttpClientProxyFactory.create(ApiService.class, configuration);
apiService.ping();
```