package com.zhp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 臧浩鹏
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@MapperScan("com.zhp.mapper")
public class AmallApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmallApplication.class, args);
	}
}
