/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: RedisPool
 * Author:   臧浩鹏
 * Date:     2018/7/30 12:47
 * Description: 获取Redis连接池
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.common;

import com.zhp.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 〈一句话功能简述〉<br> 
 * 〈获取Redis连接池〉
 *
 * @author 臧浩鹏
 * @create 2018/7/30
 * @since 1.0.0
 */
public class RedisPool {
    private static JedisPool pool;
    /**
     *
     *
     * @Description:
     * 最大连接数
     *
     * 在jedispool中最大的idle状态(空闲的)的jedis实例的个数
     *
     * 在jedispool中最小的idle状态(空闲的)的jedis实例的个数
     *
     * 在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
     *
     * 在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。
     * @auther: 臧浩鹏
     * @date: 12:49 2018/7/30
     * @param:
     * @return:
     *
     */
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20"));

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        config.setBlockWhenExhausted(true);

        pool = new JedisPool(config,redisIp,redisPort,1000*2);
    }

    static{
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }


    public static void returnBrokenResource(Jedis jedis){
        pool.close();
    }



    public static void returnResource(Jedis jedis){
        pool.close();
    }


    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("dapor","666");
        returnResource(jedis);

        pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end");


    }
}
