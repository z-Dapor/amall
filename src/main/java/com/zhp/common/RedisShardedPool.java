/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: RedisShardedPool
 * Author:   臧浩鹏
 * Date:     2018/7/31 9:44
 * Description: 分布式的RedisPool
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.common;

import com.zhp.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈分布式的RedisPool〉
 *
 * @author 臧浩鹏
 * @create 2018/7/31
 * @since 1.0.0
 */
public class RedisShardedPool {
    //shared jedis 连接池
    private static ShardedJedisPool pool;
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

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。

        config.setBlockWhenExhausted(true);

        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port);
        List<JedisShardInfo> shards = new ArrayList<>(2);
        shards.add(info1);
        shards.add(info2);
        //MURMUR_HASH 对应的一致性算法
        pool = new ShardedJedisPool(config,shards, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static{
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }


    public static void returnBrokenResource(ShardedJedis jedis){
        pool.close();
    }



    public static void returnResource(ShardedJedis jedis){
        pool.close();
    }


    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        String res = jedis.get("1");
        System.out.println(res);

        returnResource(jedis);
        pool.destroy();//临时调用，销毁连接池中的所有连接
        System.out.println("program is end");


    }

}
