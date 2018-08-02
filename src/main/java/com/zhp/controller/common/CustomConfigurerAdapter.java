/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CustomConfigurerAdapter
 * Author:   臧浩鹏
 * Date:     2018/7/30 18:39
 * Description: 自定义过滤器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.common;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.ArrayList;
import java.util.List;

;

/**
 * 〈一句话功能简述〉<br> 
 * 〈自定义过滤器〉
 *
 * @author 臧浩鹏
 * @create 2018/7/30
 * @since 1.0.0
 */
@Configuration
public class CustomConfigurerAdapter  {
    @Bean
    public FilterRegistrationBean SessionExpireFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setName("SessionExpireFilter");
        SessionExpireFilter sessionExpireFilter = new SessionExpireFilter();
        registrationBean.setFilter(sessionExpireFilter);
        registrationBean.setOrder(1);
        List<String> urlList = new ArrayList<>();
        urlList.add("*.do");
        registrationBean.setUrlPatterns(urlList);
        return registrationBean;
    }
}
