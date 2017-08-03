package com.earphone.wrapper;

import com.earphone.common.constant.ResultType;
import com.earphone.common.exception.NonCaptureException;
import com.earphone.wrapper.WrapRecord.WrapRecordBuilder;
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

/**
 * @author yaojiamin
 * @description
 * @createTime 2015-12-2 下午5:05:52
 */
@Aspect
@Slf4j
@Component
public class WrapModelAspect {
    WrapModelAspect() {
        log.info("####################Initial ResultWrapAspect####################");
    }

    private static final ThreadLocal<WrapRecordBuilder> WRAP_RECORD = ThreadLocal.withInitial(WrapRecord::builder);
    // 切面表达式
    private static final String CUT_EXPRESSION = "@annotation(org.springframework.web.bind.annotation.RequestMapping) and @annotation(com.earphone.wrapper.WrapPoint)";

    //会调用两次
    @Around(CUT_EXPRESSION)
    //@Before(CUT_EXPRESSION)
    //@AfterThrowing(value = CUT_EXPRESSION, throwing = "exception")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        WrapPoint annotation = method.getAnnotation(WrapPoint.class);
        boolean forceFailure = annotation.forceFailure();
        try {
            //全限定方法名
            String signature = method.getDeclaringClass().getName().concat(method.getName()).concat("()");
            WRAP_RECORD.get().signature(signature).point(annotation.value()).serialize(annotation.serialize()).arguments(joinPoint.getArgs());
            Object result = joinPoint.proceed(joinPoint.getArgs());
            if (annotation.wrapped()) {
                return WrappedModel.builder().result(result).type(forceType(forceFailure, SUCCESS)).build();
            }
            return result;
        } catch (NonCaptureException e) {
            return WrappedModel.builder().error(e.getMessage()).type(forceType(forceFailure, FAILURE)).build();
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            return WrappedModel.builder().error(e.getMessage()).type(forceType(forceFailure, FAILURE)).build();
        }
    }

    private ResultType forceType(boolean forceFailure, ResultType realType) {
        return forceFailure ? FAILURE : realType;
    }

    @AfterReturning(value = CUT_EXPRESSION, returning = "result")
    public void after(JoinPoint joinPoint, Object result) throws Throwable {
        WRAP_RECORD.get().result(result).build().record();
        WRAP_RECORD.remove();
    }
}
