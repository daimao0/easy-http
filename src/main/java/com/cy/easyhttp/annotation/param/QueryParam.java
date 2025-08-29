package com.cy.easyhttp.annotation.param;

import java.lang.annotation.*;

/**
 * QueryParam,查询参数常用于post的form和get的param
 *
 * @author cy
 * @since v1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryParam {
    /**
     * 参数名
     *
     * @return 参数名
     */
    String value();

    /**
     * 是否必须
     *
     * @return 是否必须
     */
    boolean required() default true;
}
