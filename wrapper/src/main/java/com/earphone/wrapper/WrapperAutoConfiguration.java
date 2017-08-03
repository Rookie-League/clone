package com.earphone.wrapper;

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
    public WrapModelAspect resultWrapAspect() {
        return new WrapModelAspect();
    }
}
