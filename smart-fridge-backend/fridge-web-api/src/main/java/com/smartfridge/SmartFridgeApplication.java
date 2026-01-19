package com.smartfridge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 智能冰箱菜谱系统 - 启动类
 */
@SpringBootApplication
@MapperScan("com.smartfridge.domain.mapper")
@EnableScheduling
public class SmartFridgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartFridgeApplication.class, args);
        System.out.println("====================================");
        System.out.println("  智能冰箱菜谱系统启动成功！");
        System.out.println("  API 文档: http://localhost:8080/doc.html");
        System.out.println("  健康检查: http://localhost:8080/api/health");
        System.out.println("====================================");
    }
}
