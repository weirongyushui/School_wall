package org.example.web.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis 配置类
 * 显式定义 SqlSessionFactory 和 SqlSessionTemplate 两个 Bean。
 * - 加载位置：classpath*:mybatis/*.xml（即 resources/mybatis 下所有 mapper xml）
 * - 数据源由 Spring Boot 自动配置的 HikariCP 提供
 * 注意：项目根目录下的 mybatis-config.xml 仅被 src/main/java/Test/ 下的手写测试类使用，
 *      Spring Boot 主流程走的是这里的 SqlSessionFactoryBean。
 */
@Configuration
public class MyBatisConfig {

    /**
     * 创建 SqlSessionFactory
     * @param dataSource Spring 自动注入的数据源
     * @return SqlSessionFactory 实例
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        // 加载 classpath 下 mybatis 目录的全部 *Mapper.xml
        sessionFactoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/*.xml")
        );
        return sessionFactoryBean.getObject();
    }

    /**
     * 创建 SqlSessionTemplate（线程安全，Spring 事务管理使用）
     * @param sqlSessionFactory 上面的 SqlSessionFactory
     * @return SqlSessionTemplate 实例
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
