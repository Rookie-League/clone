package com.earphone.wrapper.aspect;

import com.earphone.common.constant.ResultType;
import com.earphone.common.exception.NonCaptureException;
import com.earphone.wrapper.annotation.LogPoint;
import com.earphone.wrapper.wrapper.ResultWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.earphone.common.constant.ResultType.FAILURE;
import static com.earphone.common.constant.ResultType.SUCCESS;
import static com.earphone.common.utils.JSONExtend.asPrettyJSON;

/**
 * @author yaojiamin
 * @description
 * @createTime 2015-12-2 下午5:05:52
 */
@Aspect
@Slf4j
@Component
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
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        LogPoint annotation = method.getAnnotation(LogPoint.class);
        boolean forceFailure = annotation.forceFailure();
        try {
            log.info("\nInvoke:LogPoint=[{}]", annotation.value());
            Object[] args = joinPoint.getArgs();
            if (annotation.serialize()) {
                log.info("\nInvoke:Argument={}", asPrettyJSON(args));
            }
            Object result = joinPoint.proceed(args);
            if (annotation.wrapped()) {
                return ResultWrapper.builder().result(result).type(forceType(forceFailure, SUCCESS)).build();
            }
            return result;
        } catch (NonCaptureException e) {
            return ResultWrapper.builder().error(e.getMessage()).type(forceType(forceFailure, FAILURE)).build();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return ResultWrapper.builder().error(e.getMessage()).type(forceType(forceFailure, FAILURE)).build();
        }
    }

    private ResultType forceType(boolean forceFailure, ResultType realType) {
        return forceFailure ? FAILURE : realType;
    }

    @AfterReturning(value = CUT_EXPRESSION, returning = "result")
    public void after(JoinPoint joinPoint, Object result) throws Throwable {
        if (notBasicType(result)) {
            log.info("\nReturn:{}", asPrettyJSON(result));
        } else {
            log.info("\nReturn:{}", result);
        }
    }

    private boolean notBasicType(Object result) {
        return !(result instanceof CharSequence) && !(result instanceof Number) && !(result instanceof Character) && !(result instanceof Boolean);
    }
}
