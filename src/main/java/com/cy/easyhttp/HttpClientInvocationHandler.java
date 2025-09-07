package com.cy.easyhttp;

import com.cy.easyhttp.annotation.method.Delete;
import com.cy.easyhttp.annotation.method.Get;
import com.cy.easyhttp.annotation.method.Post;
import com.cy.easyhttp.annotation.method.Put;
import com.cy.easyhttp.annotation.param.HeaderParam;
import com.cy.easyhttp.annotation.param.JsonBody;
import com.cy.easyhttp.annotation.param.PathParam;
import com.cy.easyhttp.annotation.param.QueryParam;
import com.cy.easyhttp.util.HttpUtil;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Http客户端调用处理器，实现动态代理的逻辑
 *
 * @author cy
 * @since v1.0.0
 */
public class HttpClientInvocationHandler implements InvocationHandler {

    private final String baseUrl;
    private final Map<String, String> defaultHeaders;
    private final HttpUtil httpUtil;
    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("\\{([^/]+?)}");
    private final static Set<String> JAVA_LANG_CLASS_NAMES = new HashSet<>();

    static {
        JAVA_LANG_CLASS_NAMES.add("java.lang.String");
        JAVA_LANG_CLASS_NAMES.add("java.lang.Integer");
        JAVA_LANG_CLASS_NAMES.add("java.lang.Long");
        JAVA_LANG_CLASS_NAMES.add("java.lang.Short");
        JAVA_LANG_CLASS_NAMES.add("java.lang.Byte");
        JAVA_LANG_CLASS_NAMES.add("java.lang.Double");
        JAVA_LANG_CLASS_NAMES.add("java.lang.Float");
        JAVA_LANG_CLASS_NAMES.add("java.lang.Boolean");
        JAVA_LANG_CLASS_NAMES.add("java.lang.Character");
    }

    public HttpClientInvocationHandler(Class<?> clazz) {
        HttpClient annotation = clazz.getAnnotation(HttpClient.class);
        // 基础url
        this.baseUrl = annotation.baseUrl().endsWith("/") ? annotation.baseUrl().substring(0, annotation.baseUrl().length() - 1) : annotation.baseUrl();
        this.defaultHeaders = parseHeaders(annotation.headers());
        this.httpUtil = new HttpUtil(new OkHttpClient());
    }

    public HttpClientInvocationHandler(Class<?> clazz, HttpClientConfiguration configuration) {
        HttpClient annotation = clazz.getAnnotation(HttpClient.class);
        // 基础url
        if (configuration.getBaseUrl() == null || configuration.getBaseUrl().isEmpty()) {
            this.baseUrl = annotation.baseUrl().endsWith("/") ? annotation.baseUrl().substring(0, annotation.baseUrl().length() - 1) : annotation.baseUrl();
        } else {
            this.baseUrl = configuration.getBaseUrl().endsWith("/") ?
                    configuration.getBaseUrl().substring(0, configuration.getBaseUrl().length() - 1) : configuration.getBaseUrl();
        }
        this.defaultHeaders = parseHeaders(annotation.headers());
        this.defaultHeaders.putAll(configuration.getHeaders());
        this.httpUtil = configuration.getOkHttpClient() == null ? new HttpUtil(new OkHttpClient()) : new HttpUtil(configuration.getOkHttpClient());
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        //解析方法上的HTTP注解
        //构建请求路径
        String path = buildUrl(method);
        //解析url中的占位符,如/path/{id}
        path = parsePath(method, path, args);
        //构建请求头
        Map<String, String> headers = buildHeaders(method, args);
        //调用请求
        if (method.isAnnotationPresent(Get.class)) {
            Map<String, String> params = buildGetParams(method, args);
            return httpUtil.doGet(path, headers, params, method.getGenericReturnType());
        }
        if (method.isAnnotationPresent(Post.class)) {
            Object jsonBody = buildJsonBody(method, args);
            return httpUtil.doPost(path, headers, jsonBody, method.getGenericReturnType());
        }
        if (method.isAnnotationPresent(Put.class)) {
            Object jsonBody = buildJsonBody(method, args);
            return httpUtil.doPut(path, headers, jsonBody, method.getGenericReturnType());
        }
        if (method.isAnnotationPresent(Delete.class)) {
            Map<String, String> params = buildGetParams(method, args);
            return httpUtil.doDelete(path, headers, params, method.getGenericReturnType());
        }
        return null;
    }

    /**
     * 构建完整URL
     */
    private String buildUrl(Method method) {
        String path = "";

        if (method.isAnnotationPresent(Get.class)) {
            path = method.getAnnotation(Get.class).value();
        }
        if (method.isAnnotationPresent(Post.class)) {
            path = method.getAnnotation(Post.class).value();
        }
        if (method.isAnnotationPresent(Put.class)) {
            path = method.getAnnotation(Put.class).value();
        }
        if (method.isAnnotationPresent(Delete.class)) {
            path = method.getAnnotation(Delete.class).value();
        }
        // 拼接baseUrl和path，处理斜杠问题
        return baseUrl + (path.startsWith("/") ? "" : "/") + path;
    }

    /**
     * 解析url路径的占位符
     * /path/{id}
     *
     * @param method 方法
     * @param path   请求路径
     * @return 构建后的路径
     */
    private String parsePath(Method method, String path, Object[] args) {
        //获取url路径参数
        Map<String, String> pathVars = getStringStringMap(method, args);

        // 替换 {xxx} 占位符
        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(path);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // 提取 { } 中的名字
            String varName = matcher.group(1);
            String varValue = pathVars.get(varName);
            if (varValue == null) {
                throw new IllegalArgumentException("No value provided for path variable: {" + varName + "}");
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(varValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取路径参数
     *
     * @param method 方法
     * @param args   参数
     * @return 路径参数
     */
    @NotNull
    private static Map<String, String> getStringStringMap(Method method, Object[] args) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        Map<String, String> pathVars = new HashMap<>();

        // 收集所有 @PathParam 参数
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation annotation : paramAnnotations[i]) {
                if (annotation instanceof PathParam) {
                    String paramName = ((PathParam) annotation).value();
                    Object paramValue = args[i];
                    if (paramValue == null) {
                        throw new IllegalArgumentException("Path variable '" + paramName + "' cannot be null");
                    }
                    pathVars.put(paramName, paramValue.toString());
                }
            }
        }
        return pathVars;
    }

    /**
     * 设置请求头
     */
    private Map<String, String> buildHeaders(Method method, Object[] args) {
        // 添加默认请求头
        if (method.isAnnotationPresent(Get.class)) {
            defaultHeaders.putAll(parseHeaders(method.getAnnotation(Get.class).headers()));
        }
        if (method.isAnnotationPresent(Post.class)) {
            defaultHeaders.putAll(parseHeaders(method.getAnnotation(Post.class).headers()));
        }
        if (method.isAnnotationPresent(Put.class)) {
            defaultHeaders.putAll(parseHeaders(method.getAnnotation(Put.class).headers()));
        }
        if (method.isAnnotationPresent(Delete.class)) {
            defaultHeaders.putAll(parseHeaders(method.getAnnotation(Delete.class).headers()));
        }
        //处理参数请求头
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof HeaderParam) {
                    defaultHeaders.put(((HeaderParam) annotation).value(), args[i].toString());
                }
            }
        }
        return defaultHeaders;
    }

    /**
     * 构建Get请求参数
     * 常见: /path?param1=value1&param2=value2
     *
     * @param method 请求方法
     * @param args   参数
     * @return 返回请求中的查询参数
     */
    private Map<String, String> buildGetParams(Method method, Object[] args) {
        if (args==null || args.length==0){
           return new HashMap<>();
        }
        Map<String, String> params = new HashMap<>();
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof QueryParam) {
                    if (args[i] != null) {
                        params.put(((QueryParam) annotation).value(), args[i].toString());
                    }
                    break;
                }
            }
        }
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            Class<?> currentClass = arg.getClass();
            Annotation[] annotation = annotations[i];

            if (!JAVA_LANG_CLASS_NAMES.contains(currentClass.getName()) && annotation.length == 0) {
                // 遍历当前类及其所有父类（直到Object类）
                while (currentClass != null && currentClass != Object.class) {
                    Field[] declaredFields = currentClass.getDeclaredFields();
                    for (Field field : declaredFields) {
                        String fieldName = field.getName();
                        String getterName = buildGetterName(field);

                        try {
                            // 尝试获取并调用当前类的getter方法
                            Object value = getFieldValue(arg, currentClass, getterName, field);
                            if (value != null) {
                                params.put(fieldName, String.valueOf(value));
                            }
                        } catch (Exception e) {
                            // 处理异常（如无对应getter方法等）
                            // System.err.println("无法获取字段 " + fieldName + " 的值: " + e.getMessage());
                        }
                    }
                    // 移动到父类
                    currentClass = currentClass.getSuperclass();
                }
            }
        }
        return params;
    }
    // 工具方法：构建getter方法名
    private static String buildGetterName(Field field) {
        String fieldName = field.getName();
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            return "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        } else {
            return "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        }
    }

    // 工具方法：获取字段值（支持父类方法）
    private static Object getFieldValue(Object obj, Class<?> clazz, String getterName, Field field)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try {
            // 尝试调用当前类的getter方法
            return clazz.getMethod(getterName).invoke(obj);
        } catch (NoSuchMethodException e) {
            // 如果当前类没有该方法，且是boolean类型，尝试getXxx()形式
            if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                String altGetterName = "get" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
                return clazz.getMethod(altGetterName).invoke(obj);
            }
            throw e;
        }
    }

    /**
     * 构建JSON请求体
     *
     * @param method 请求方法
     * @param args   请求参数
     * @return 请求体
     */
    private Object buildJsonBody(Method method, Object[] args) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation ann : annotations[i]) {
                if (ann instanceof JsonBody) {
                    return args[i];
                }
            }
        }
        return null;
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
