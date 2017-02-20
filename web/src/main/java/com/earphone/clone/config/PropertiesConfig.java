package com.earphone.clone.config;

import com.earphone.common.constant.Charset;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/1/17
 * @createTime 17:39
 */
@Configuration
public class PropertiesConfig {

    private static PropertySourcesPlaceholderConfigurer createPlaceholder(Resource... location) {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocations(location);
        configurer.setIgnoreResourceNotFound(false);
        configurer.setFileEncoding(Charset.UTF_8.getCharset());
        return configurer;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer applicationConfigs() throws IOException {
        return createPlaceholder(transactionResource(), redisResource(), quartzResource());
    }

    private static Resource transactionResource() {
        return new ClassPathResource("properties/transaction.properties");
    }

    private static Resource redisResource() {
        return new ClassPathResource("properties/redis.properties");
    }

    private static Resource quartzResource() {
        return new ClassPathResource("properties/quartz.properties");
    }

}
