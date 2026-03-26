package com.example.foodordersystem.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.example.foodordersystem.controller.*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* com.example.foodordersystem.service.*.*(..))")
    public void serviceMethods() {}

    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Entering controller: {}.{} with args: {}",
                className, methodName, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            log.info("Exiting controller: {}.{} with result type: {} in {}ms",
                    className, methodName,
                    result != null ? result.getClass().getSimpleName() : "null",
                    endTime - startTime);

            return result;
        } catch (Exception e) {
            log.error("Exception in controller: {}.{} - {}",
                    className, methodName, e.getMessage(), e);
            throw e;
        } finally {
            MDC.remove("traceId");
        }
    }

    @Around("serviceMethods()")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("Entering service: {}.{}", className, methodName);

        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();

            log.debug("Exiting service: {}.{} in {}ms",
                    className, methodName, endTime - startTime);

            return result;
        } catch (Exception e) {
            log.error("Exception in service: {}.{} - {}",
                    className, methodName, e.getMessage(), e);
            throw e;
        }
    }

}

