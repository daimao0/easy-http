package com.cy.easyhttp.annotation.param;

import java.lang.annotation.*;

/**
 * 请求头参数
 *
 * @author cy
 * @since v1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HeaderParam {
    String value();
}
