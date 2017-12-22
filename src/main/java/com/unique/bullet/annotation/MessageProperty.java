package com.unique.bullet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageProperty {
    String name();

    // 动态值
    String jxpath() default "";

    // 如果jxpath为空，则取静态值value
    String value() default "";
}
