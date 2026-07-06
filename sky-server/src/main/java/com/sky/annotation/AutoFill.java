package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 表示该注解只能用于方法
@Retention(RetentionPolicy.RUNTIME) // 表示该注解在运行时保留
public @interface AutoFill {

    // 设置数据库操作类型：插入或更新
    OperationType value();//注解中的 value() 是一个特殊方法，用于定义注解的属性。它可以像字段一样使用，支持默认值和简化语法。
    /**
     * 注解的两大功效：1.匹配方法，看用在那个方法（或类）上
     * 2.属性值，如果对于匹配到的方法要执行不同的操作，就要在切面类中定义不同的逻辑实现，而这要用注解的属性值来区分，即value值
     * 如@AutoFill(OperationType.INSERT)，里面的属性值
     */
}
/**
 * 在注解（@interface）中，value() 是一个特殊的方法，用于定义注解的属性。虽然它看起来像普通方法，
 * 但在注解中它的作用更像是一个字段。
 */