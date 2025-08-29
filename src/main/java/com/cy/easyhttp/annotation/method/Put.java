package com.cy.easyhttp.annotation.method;

import java.lang.annotation.*;

/**
 * POST请求注解
 *
 * @author cy
 * @since v1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Put {
    /**
     * 请求路径
     */
    String value();

    /**
     * 额外请求头
     */
    String[] headers() default {};
}
