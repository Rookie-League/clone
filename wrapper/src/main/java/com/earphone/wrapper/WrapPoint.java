package com.earphone.wrapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YaoJiamin
 * @description 使用了这个注解以后，一定会记录方法返回值，如果wrapped为true，则方法返回类型一定要设置为Object
 * @createDate 2017/2/7
 * @createTime 17:33
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WrapPoint {
    /**
     * 这个值一般用于记录一些与目标注解方法相关的信息,
     * 比如方法业务描述或功能描述或参考数据或其他信息。
     *
     * @return 自定义信息
     */
    String value() default "";

    /**
     * 这个值定义了是否要对返回值进行包装，默认为true，
     * 如不需进行包装，可改为false，返回值会保持原始结构返回。
     *
     * @return 是否要对返回值进行包装
     */
    boolean wrapped() default true;

    /**
     * 强行设置返回结果为失败，不管实际执行是否成功
     *
     * @return 返回结果
     */
    boolean forceFailure() default false;

    /**
     * 是否序列化参数
     *
     * @return 返回结果
     */
    boolean serialize() default true;
}
