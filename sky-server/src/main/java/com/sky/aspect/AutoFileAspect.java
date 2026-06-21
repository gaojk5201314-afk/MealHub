package com.sky.aspect;


import com.sky.anno.anno;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.annotation.AnnotationDescription;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;

/*
自定义切面类，用于自动上传文件
* */

@Aspect
@Slf4j
@Component
public class AutoFileAspect {


    /**
     * 自定义切点，用于匹配所有service层的方法，先匹配注解，再匹配方法名，速度更快
     */
    /*快速方法*/
    @Pointcut("execution(* com.sky.service.impl.*.*(..)) && @annotation(com.sky.anno.anno)")
    public void AutoFilePointcut() {
    }
    @Before("AutoFilePointcut()")
    public void before(JoinPoint jp) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.info("========== 开始进行公共字段自动填充 ==========");
        
        /*获取方法签名对象*/
        MethodSignature signature = (MethodSignature) jp.getSignature();
        log.info("拦截的方法：{}", signature.getMethod().getName());
        
        /*获取方法上的注解对象*/
        anno annotation = signature.getMethod().getAnnotation(anno.class);
        if (annotation == null) {
            log.warn("注解为空，无法进行自动填充");
            return;
        }
        
        /*获取注解对象的值*/
        OperationType value = annotation.value();
        log.info("操作类型：{}", value);

        /*获取当前被拦截的方法参数，实体对象*/
        Object[] args = jp.getArgs();
        log.info("方法参数数量：{}", args != null ? args.length : 0);

        if(args == null || args.length == 0){
            log.warn("方法参数为空，无法进行自动填充");
            return;
        }
        
        log.info("第一个参数类型：{}", args[0].getClass().getName());

        /*准备赋值的数据*/
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        log.info("当前时间：{}, 当前用户ID：{}", now, currentId);

        /*分情况赋值*/
        if(value == OperationType.UPDATE){
            Method setcreattime = args[0].getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setcraetuser = args[0].getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            /*通过反射为对象赋值*/
            setcreattime.invoke(args[0], now);
            setcraetuser.invoke(args[0], currentId);
        }else if(value == OperationType.INSERT){
            Method setcreattime = args[0].getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setupdatetime = args[0].getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setcraetuser = args[0].getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setupdatauser = args[0].getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            /*通过反射为对象赋值*/
            setcreattime.invoke(args[0], now);
            setupdatetime.invoke(args[0], now);
            setupdatauser.invoke(args[0], currentId);
            setcraetuser.invoke(args[0], currentId);
        }

    }
}