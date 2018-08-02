/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: SpringSessionConfig
 * Author:   臧浩鹏
 * Date:     2018/7/31 18:32
 * Description: springsession的配置类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 〈一句话功能简述〉<br> 
 * 〈springsession的配置类〉
 *
 * @author 臧浩鹏
 * @create 2018/7/31
 * @since 1.0.0
 */
@Configuration
@EnableRedisHttpSession
public class SpringSessionConfig {
    @Bean
    public DefaultCookieSerializer defaultCookieSerializer() {
        DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
        defaultCookieSerializer.setCookieMaxAge(31536000);
        defaultCookieSerializer.setCookiePath("/");
        defaultCookieSerializer.setDomainName("amall.com");
        return defaultCookieSerializer;
    }
}
