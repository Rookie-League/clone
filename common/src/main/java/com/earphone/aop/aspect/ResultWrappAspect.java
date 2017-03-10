package com.earphone.aop.aspect;

import com.earphone.aop.wrapper.ResultWrapper.ResultWrapperBuilder;
import com.earphone.common.constant.ResultType;
import com.earphone.common.exception.NonCaptureException;
import com.earphone.utility.utils.JSONUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yaojiamin
 * @description
 * @createTime 2015-12-2 下午5:05:52
 */
@Aspect
public class ResultWrappAspect {
    // 日志对象
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // 切面表达式
    private final String CUT_EXPRESSION = "@annotation(org.springframework.web.bind.annotation.RequestMapping) and @annotation(com.earphone.aop.annotation.LogPoint)";

    //会调用两次
    @Around(CUT_EXPRESSION)
    //@Before(CUT_EXPRESSION)
    //@AfterThrowing(value = CUT_EXPRESSION, throwing = "exception")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return new ResultWrapperBuilder().setResult(joinPoint.proceed(joinPoint.getArgs())).builder();
        } catch (NonCaptureException e) {
            return new ResultWrapperBuilder().setType(ResultType.FAILURE).builder();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            return new ResultWrapperBuilder().setError(e.getMessage()).setType(ResultType.FAILURE).builder();
        }
    }

    @AfterReturning(value = CUT_EXPRESSION, returning = "result")
    public void around(Object result) throws Throwable {
        logger.info("Return:{}", JSONUtils.toJSON(result));
    }
}
