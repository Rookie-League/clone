package com.earphone.wrapper.aspect;

import com.earphone.wrapper.annotation.LogPoint;
import com.earphone.wrapper.wrapper.ResultWrapper.ResultWrapperBuilder;
import com.earphone.common.constant.ResultType;
import com.earphone.common.exception.NonCaptureException;
import com.earphone.common.utils.JSONUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author yaojiamin
 * @description
 * @createTime 2015-12-2 下午5:05:52
 */
@Aspect
public class ResultWrapAspect {
    // 日志对象
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // 切面表达式
    private final String CUT_EXPRESSION = "@annotation(org.springframework.web.bind.annotation.RequestMapping) and @annotation(com.earphone.wrapper.annotation.LogPoint)";

    //会调用两次
    @Around(CUT_EXPRESSION)
    //@Before(CUT_EXPRESSION)
    //@AfterThrowing(value = CUT_EXPRESSION, throwing = "exception")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            LogPoint annotation = method.getAnnotation(LogPoint.class);
            logger.info("Invoke:{}", annotation.value());
            Object result = joinPoint.proceed(joinPoint.getArgs());
            if (annotation.wrapped()) {
                return new ResultWrapperBuilder().setResult(result).builder();
            }
            return result;
        } catch (NonCaptureException e) {
            return new ResultWrapperBuilder().setType(ResultType.FAILURE).builder();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            return new ResultWrapperBuilder().setError(e.getMessage()).setType(ResultType.FAILURE).builder();
        }
    }

    @AfterReturning(value = CUT_EXPRESSION, returning = "result")
    public void after(JoinPoint joinPoint, Object result) throws Throwable {
        if (notBasicType(result)) {
            logger.info("Return:{}", JSONUtils.toJSON(result));
        } else {
            logger.info("Return:{}", result);
        }
    }

    private boolean notBasicType(Object result) {
        if (result instanceof String) return false;
        if (result instanceof Integer) return false;
        if (result instanceof Long) return false;
        if (result instanceof Short) return false;
        if (result instanceof Double) return false;
        if (result instanceof Float) return false;
        if (result instanceof Character) return false;
        return !(result instanceof Boolean);
    }
}