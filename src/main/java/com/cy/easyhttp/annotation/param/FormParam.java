package com.cy.easyhttp.annotation.param;

/**
 * 表单参数
 *
 * @author cy
 * @since v1.0.0
 */
public @interface FormParam {

    /**
     * 表单字段名
     *
     * @return 字段名
     */
    String value();

    /**
     * 是否是必须
     *
     * @return 是否是文件
     */
    boolean required() default true;
}
