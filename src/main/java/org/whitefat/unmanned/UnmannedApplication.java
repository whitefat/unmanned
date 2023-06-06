package org.whitefat.unmanned;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author liuyong
 */
@SpringBootApplication(exclude = DruidDataSourceAutoConfigure.class)
@MapperScan(basePackages = {"org.whitefat.unmanned.mapper"})
public class UnmannedApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnmannedApplication.class, args);
    }

}
