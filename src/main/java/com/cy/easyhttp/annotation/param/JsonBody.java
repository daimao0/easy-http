package com.cy.easyhttp.annotation.param;

import java.lang.annotation.*;

/**
 * json请求体注解
 *
 * @author cy
 * @since v1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonBody {
}
