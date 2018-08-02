/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: RedissonConfig
 * Author:   臧浩鹏
 * Date:     2018/8/1 20:09
 * Description: RedissonConfig
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.common;

import com.zhp.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 〈一句话功能简述〉<br> 
 * 〈RedissonConfig〉
 *
 * @author 臧浩鹏
 * @create 2018/8/1
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class RedissonManagerConfig {
    private Config config = new Config();

    private Redisson redisson = null;

    public Redisson getRedisson() {
        return redisson;
    }

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    @PostConstruct
    private void init(){
        try {
            config.useSingleServer().setAddress(new StringBuilder().append(redis1Ip).append(":").append(redis1Port).toString());

            redisson = (Redisson) Redisson.create(config);

            log.info("初始化Redisson结束");
        } catch (Exception e) {
            log.error("redisson init error",e);
        }
    }
}
