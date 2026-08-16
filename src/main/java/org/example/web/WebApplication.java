package org.example.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * Spring Boot 启动类
 * - @SpringBootApplication：开启自动配置、组件扫描
 * - @MapperScan：扫描 org.example.web.mapper 包下所有 Mapper 接口并注册为 Bean
 * 启动后默认端口 8081（见 application.properties），数据源指向 MySQL school_db。
 */
@SpringBootApplication
@MapperScan("org.example.web.mapper")
public class WebApplication {

    /**
     * 应用入口
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}
