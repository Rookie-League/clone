package com.earphone.wrapper;

import com.earphone.wrapper.aspect.ResultWrapAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/8/3
 * @createTime 13:50
 */
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
public class WrapperAutoConfiguration {
    @Bean
    public ResultWrapAspect resultWrapAspect() {
        return new ResultWrapAspect();
    }
}
