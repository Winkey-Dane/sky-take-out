package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：用于标识某个方法需要进行功能字段的自动填充
 */
// Target的作用是指定该注解可以用于哪些Java元素上
@Target(ElementType.METHOD)  // 该注解只能用于方法上
// Retention的作用是指定该注解在什么级别可用
@Retention(RetentionPolicy.RUNTIME) // 该注解在运行时可用
public @interface AutoFill {
    OperationType value(); // 用于指定操作类型，是插入操作还是更新操作 OperationType是枚举类
}
