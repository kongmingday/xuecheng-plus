package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages={"com.xuecheng.content.feignclient"})
public class XuchengApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(XuchengApiApplication.class, args);
    }

}
