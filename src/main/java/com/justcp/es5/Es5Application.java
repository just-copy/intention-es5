package com.justcp.es5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync // 开启异步方法调用支持
@SpringBootApplication
public class Es5Application {

    public static void main(String[] args) {
        SpringApplication.run(Es5Application.class, args);
    }

}
