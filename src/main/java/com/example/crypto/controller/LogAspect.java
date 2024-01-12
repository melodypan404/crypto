package com.example.crypto.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class LogAspect {

    @Around("execution(public * com.example.crypto.service.LoadService.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        System.out.println("around -- function name:" + point.getSignature().getName());
        System.out.println("around -- input: " + Arrays.toString(point.getArgs()));

        long k = System.currentTimeMillis();
        var res = point.proceed();
        System.out.println("around -- function takes "+ (System.currentTimeMillis()-k) + " ms");
        return res;
    }

}
