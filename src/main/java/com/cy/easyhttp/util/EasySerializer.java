package com.cy.easyhttp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 基于Jackson的序列化/反序列化工具
 *
 * @author cy
 * @since v1.0.0
 */
public class EasySerializer {
    /**
     * Jackson对象映射器
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            // 1. 不要用 Java 数组来表示 JSON 数组（除非明确指定）
            .configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, false)

            // 2. 当 JSON 中有未知字段时，不要抛异常
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

            // 3. 允许空字符串 "" 被反序列化为 null（对 String 字段有用）
            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false)

            // 4. 包装反序列化异常（可选，通常保持默认即可）
            .configure(DeserializationFeature.WRAP_EXCEPTIONS, true);

    private EasySerializer() {
    }

    /**
     * 将对象序列化为JSON字符串
     */
    public static String serialize(Object obj) {
        if (obj == null) {
            return null;
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象序列化失败", e);
        }
    }


    /**
     * 将JSON字符串反序列化为对象
     */
    public static <T> T deserialize(String json, Type type) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructType(type);
            T obj = convertNonJson(json, type);
            if (obj != null) {
                return obj;
            }
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (IOException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }

    /**
     * 非JSON字符串转换
     *
     * @param content 内容
     * @param type    要转换的类型
     * @param <T>     泛型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertNonJson(String content, Type type) {
        // 处理 String 类型
        if (type == String.class) {
            return (T) content;
        }

        // 处理 boolean / Boolean
        if (type == Boolean.class || type == boolean.class) {
            if ("true".equalsIgnoreCase(content)) {
                return (T) Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(content)) {
                return (T) Boolean.FALSE;
            }
            throw new RuntimeException("无法将 '" + content + "' 转换为 Boolean");
        }

        // 处理 int / Integer
        if (type == Integer.class || type == int.class) {
            try {
                return (T) Integer.valueOf(content);
            } catch (NumberFormatException e) {
                throw new RuntimeException("无法将 '" + content + "' 转换为 Integer");
            }
        }

        // 处理 long / Long
        if (type == Long.class || type == long.class) {
            try {
                return (T) Long.valueOf(content);
            } catch (NumberFormatException e) {
                throw new RuntimeException("无法将 '" + content + "' 转换为 Long");
            }
        }

        // 处理 double / Double
        if (type == Double.class || type == double.class) {
            try {
                return (T) Double.valueOf(content);
            } catch (NumberFormatException e) {
                throw new RuntimeException("无法将 '" + content + "' 转换为 Double");
            }
        }

        // 处理 float / Float
        if (type == Float.class || type == float.class) {
            try {
                return (T) Float.valueOf(content);
            } catch (NumberFormatException e) {
                throw new RuntimeException("无法将 '" + content + "' 转换为 Float");
            }
        }
        return null;
    }


}
    