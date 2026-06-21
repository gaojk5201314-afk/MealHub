package com.sky.anno;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*注解的注解，表示只对方法生效*/
@Target(ElementType.METHOD)
/*注解的注解，表示这个注解在运行时生效*/
@Retention(RetentionPolicy.RUNTIME)
public @interface anno {
    OperationType value();
}