/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: SessionConfig
 * Author:   臧浩鹏
 * Date:     2018/7/31 11:01
 * Description: SpringSession的配置类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.common;


import com.zhp.util.PropertiesUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

/**
 * 〈一句话功能简述〉<br> 
 * 〈SpringRedisSession的配置类〉
 *
 * @author 臧浩鹏
 * @create 2018/7/31
 * @since 1.0.0
 */
@Configuration
public class RedisSessionConfig {

    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(PropertiesUtil.getProperty("redis1.ip"));
        config.setPort(Integer.parseInt(PropertiesUtil.getProperty("redis1.port")));
        return new LettuceConnectionFactory(config);
    }
}
