package com.earphone.clone.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/1/17
 * @createTime 17:39
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.earphone"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Controller.class, Service.class})
})
public class SpringMajorConfig {

    public static void main(String[] args) {
        SpringApplication.run(SpringMajorConfig.class, args);
    }

}
