/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CheckAdInterceptorConfig
 * Author:   臧浩鹏
 * Date:     2018/8/1 9:43
 * Description: 拦截器配置类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * 〈一句话功能简述〉<br> 
 * 〈拦截器配置类〉
 *
 * @author 臧浩鹏
 * @create 2018/8/1
 * @since 1.0.0
 */
@Configuration
public class CheckAdInterceptorConfig implements WebMvcConfigurer {

    @Bean
    public CheckAdminHandlerInterceptor myInterceptor(){
        return new CheckAdminHandlerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new CheckAdminHandlerInterceptor());
        registration.addPathPatterns("/manage/**");
        registration.excludePathPatterns("/manage/user/login.do");
    }
}
