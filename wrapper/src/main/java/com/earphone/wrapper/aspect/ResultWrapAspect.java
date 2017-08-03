package com.earphone.wrapper.aspect;

import com.earphone.common.constant.ResultType;
import com.earphone.common.exception.NonCaptureException;
import com.earphone.common.utils.JSONExtend;
import com.earphone.wrapper.annotation.LogPoint;
import com.earphone.wrapper.wrapper.ResultWrapper.ResultWrapperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author yaojiamin
 * @description
 * @createTime 2015-12-2 下午5:05:52
 */
@Aspect
@Slf4j
public class ResultWrapAspect {
    public ResultWrapAspect() {
        log.info("####################Initial ResultWrapAspect####################");
    }

    // 切面表达式
    private static final String CUT_EXPRESSION = "@annotation(org.springframework.web.bind.annotation.RequestMapping) and @annotation(com.earphone.wrapper.annotation.LogPoint)";

    //会调用两次
    @Around(CUT_EXPRESSION)
    //@Before(CUT_EXPRESSION)
    //@AfterThrowing(value = CUT_EXPRESSION, throwing = "exception")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            LogPoint annotation = method.getAnnotation(LogPoint.class);
            log.info("Invoke:{}", annotation.value());
            Object result = joinPoint.proceed(joinPoint.getArgs());
            if (annotation.wrapped()) {
                return new ResultWrapperBuilder().setResult(result).builder();
            }
            return result;
        } catch (NonCaptureException e) {
            return new ResultWrapperBuilder().setError(e.getMessage()).setType(ResultType.FAILURE).builder();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return new ResultWrapperBuilder().setError(e.getMessage()).setType(ResultType.FAILURE).builder();
        }
    }

    @AfterReturning(value = CUT_EXPRESSION, returning = "result")
    public void after(JoinPoint joinPoint, Object result) throws Throwable {
        if (notBasicType(result)) {
            log.info("Return:{}", JSONExtend.asJSON(result));
        } else {
            log.info("Return:{}", result);
        }
    }

    private boolean notBasicType(Object result) {
        if (result instanceof String) {
            return false;
        }
        if (result instanceof Integer) {
            return false;
        }
        if (result instanceof Long) {
            return false;
        }
        if (result instanceof Short) {
            return false;
        }
        if (result instanceof Double) {
            return false;
        }
        if (result instanceof Float) {
            return false;
        }
        if (result instanceof Character) {
            return false;
        }
        return !(result instanceof Boolean);
    }
}
