package com.cy.easyhttp.annotation.param;

import java.lang.annotation.*;

/**
 * URL路径参数
 *
 * @author cy
 * @since v1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathParam {

    /**
     * 参数名称，url中的占位符
     * 如用户URL:/test/{id}
     * return id
     *
     * @return 参数名称
     */
    String value();
}
