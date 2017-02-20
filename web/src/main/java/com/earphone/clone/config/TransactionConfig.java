package com.earphone.clone.config;

import com.earphone.common.plugin.pagination.CacheInterceptor;
import com.earphone.common.plugin.pagination.MybatisInterceptor;
import com.earphone.common.plugin.pagination.PaginationInterceptor;
import com.earphone.common.plugin.pagination.ResultSetInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author YaoJiamin
 * @description
 * @createDate 2017/1/19
 * @createTime 13:02
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.earphone"}, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {Service.class})
})
@MapperScan({
        "com.earphone.clone.module.**.mapper"
})
public class TransactionConfig {

    @Resource
    private DataSource dataSource;

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(ApplicationContext context) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setConfigLocation(new ClassPathResource("mybatis-config.xml"));
        factoryBean.setMapperLocations(context.getResources("classpath*:/com/earphone/clone/module/**/*.xml"));
        factoryBean.setPlugins(new Interceptor[]{cacheInterceptor(), paginationInterceptor(), resultSetInterceptor()});
        return factoryBean;
    }

    @Bean
    public MybatisInterceptor cacheInterceptor() {
        return new CacheInterceptor();
    }

    @Bean
    public MybatisInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public MybatisInterceptor resultSetInterceptor() {
        return new ResultSetInterceptor();
    }

    @Bean
    public DataSourceTransactionManager transactionManager() throws Exception {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

}
