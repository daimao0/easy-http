package com.cy.easyhttp;

import java.lang.annotation.*;

/**
 * 标记接口为HTTP客户端
 *
 * @author cy
 * @since v1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpClient {
    /**
     * 基础URL
     */
    String baseUrl();

    /**
     * 默认请求头
     */
    String[] headers() default {};
}
