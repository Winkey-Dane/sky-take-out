package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 自动填充切面 实现对数据库插入和更新操作时的自动填充功能
 */

@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    // mapper包下所有方法 (..)代表任意参数  在满足第一个条件下，方法上要有这个注解
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && " +
            "( @annotation(com.sky.annotation.AutoFill) )")
    public void autoFillPointCut() {
    }

    // 赋值的逻辑写在通知当中

    // 通知使用前置通知 @Before 原因：要在执行插入和更新操作之前进行赋值，
    // 即进行create_time和create_user的更新操作
    @Before("autoFillPointCut()") // 引用切入点
    public void autoFill(JoinPoint joinPoint) {
        // joinPoint为连接点，可以获取到方法的相关信息
        log.info("自动填充开始....");

        // 获取 被拦截的方法上的 操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class); // 反射的代码
        OperationType value = autoFill.value(); // 获取操作类型
        log.info("操作类型：{}", value);

        // 获取到当前 被拦截的方法的参数列表 即实体对象
        Object[] args = joinPoint.getArgs();
        if (args == null && args.length == 0) {
            return;
        }
        Object entity = args[0]; // 约定第一个参数为实体对象
        log.info("实体对象：{}", entity);

        // 准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        // LocalDateTime update_time = LocalDateTime.now();
        Long create_userId = BaseContext.getCurrentId();
        Long update_userId = BaseContext.getCurrentId();

        // 根据当前不同的操作类型 为对应属性通过反射机制 进行赋值
        if (value == OperationType.INSERT) {
            // 插入操作 需要填充四个属性
            try {
                // 反射机制进行赋值
                // 创建时间
                Method setCreateTime = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                setCreateTime.invoke(entity, now);

                // 修改更新时间
                Method setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                setUpdateTime.invoke(entity, now);

                // 创建人
                Method setCreateUserId = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                setCreateUserId.invoke(entity, create_userId);

                // 修改人
                Method setUpdateUserId = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateUserId.invoke(entity, update_userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (value == OperationType.UPDATE) {
            // 更新操作 需要填充两个属性
            try {
                // 修改时间
                Method setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                setUpdateTime.invoke(entity, now);
                // 修改人
                Method setUpdateUserId = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateUserId.invoke(entity, update_userId);
                log.info("修改过的实体对象：{}", entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
