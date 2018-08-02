/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CloseOrderTask
 * Author:   臧浩鹏
 * Date:     2018/8/1 14:03
 * Description: 定时关单任务器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.task;

import com.zhp.common.Const;
import com.zhp.controller.common.RedissonManagerConfig;
import com.zhp.service.IOrderService;
import com.zhp.util.DateTimeUtil;
import com.zhp.util.PropertiesUtil;
import com.zhp.util.RedisSharderPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br> 
 * 〈定时关单任务器〉
 *
 * @author 臧浩鹏
 * @create 2018/8/1
 * @since 1.0.0
 */
@Component
@Slf4j
public class CloseOrderTask {


    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private RedissonManagerConfig redissonManagerConfig;

    //@Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderV1() {
        /**
         *
         * @Description: 此版本没有锁
         *
         * @auther: 臧浩鹏
         * @date: 15:28 2018/8/1
         * @param: []
         * @return: void
         *
         */
        log.info("现在时间：", DateTimeUtil.dateToStr(new Date()));
        iOrderService.closeOrder(Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2")));
        log.info("已关闭一批超时订单");
    }

    /**
     *
     * @Description: 构建分布式锁
     *
     * @auther: 臧浩鹏
     * @date: 16:08 2018/8/1
     * @param: []
     * @return: void
     *
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderV2() {
        /**
         *
         * @Description: 此版本有锁 但存在一个问题：就是当刚设置完锁刚被setnx后 tomcat突然被shutdown，那么下一次启动时 将无法继续服务
         *              ，但可以通过@PreDestroy来进行调用delkey方法，但是这样存在来不及管删除的状况;还有种情况时kill暴力结束tomcat进程；那么将不会执行
         *              delkey方法；所以此分布式锁算法存在很大漏洞
         *
         * @auther: 臧浩鹏
         * @date: 15:28 2018/8/1
         * @param: []
         * @return: void
         *
         */
        log.info("现在时间：", DateTimeUtil.dateToStr(new Date()));
        Long outTime = Long.parseLong(PropertiesUtil.getProperty("lock.timeout"));
        Long setNxResult = RedisSharderPoolUtil.setNx(Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK, String.valueOf(System.currentTimeMillis() + outTime));
        if(setNxResult != null && setNxResult.intValue() ==1){
            //获取到锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK);
        }else {
            log.info("获取锁失败！");
        }
        log.info("已关闭一批超时订单");
    }

    @PreDestroy
    public void delKey(String key){
        RedisSharderPoolUtil.del(key);
    }


    public void closeOrderV3() {
        /**
         *
         * @Description: 此版本为优化版本 双重防死锁：当获取锁失败时不像之前版本那样直接放弃，而是通过get(Key)方法获取lockName的valueA，若value不为null
         *               并且当前的currentTime>valueA的结果为：true==>则说明该lock键已经过期，可以进行设置；此时再进行getset(lockKey,currenttime+out)
         *                                                           若返回值ValueB的值为null || valueB == valueA
         *                                                              结果为true==>那么进行expire,执行业务，删除键的业务流程
         *                                                              结果为false==>那么放弃执行
         *                                                    false==>否则放弃获取锁
         *
         * 小瑕疵：timeOut不宜设置过长 会浪费时间：当重启机器后没有大于Value，那么会重新获取锁
         * @auther: 臧浩鹏
         * @date: 15:28 2018/8/1
         * @param: []
         * @return: void
         *
         */
        log.info("现在时间：", DateTimeUtil.dateToStr(new Date()));
        Long outTime = Long.parseLong(PropertiesUtil.getProperty("lock.timeout"));
        Long setNxResult = RedisSharderPoolUtil.setNx(Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK, String.valueOf(System.currentTimeMillis() + outTime));
        if(setNxResult != null && setNxResult.intValue() ==1){
            //获取到锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK);
        }else {
            String ValueA = "";
            if((ValueA = RedisSharderPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK))!=null && System.currentTimeMillis() > Long.parseLong(ValueA)){
                String ValueB = RedisSharderPoolUtil.getAndSet(Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK, String.valueOf(System.currentTimeMillis() + outTime));
                if(StringUtils.equals(ValueA,ValueB) || ValueB==null){
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK);
                }else {
                    log.info("获取锁失败！");
                }
            }else {
                log.info("获取锁失败！");
            }
        }
        log.info("关闭超时订单任务结束");
    }


    private void closeOrder(String keyName){
        RedisSharderPoolUtil.expire(keyName,50);
        log.info("当前线程：{}，获取{}锁",Thread.currentThread().getName(),keyName);
        iOrderService.closeOrder(Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2")));
        RedisSharderPoolUtil.del(keyName);
        log.info("释放{}锁",keyName);
    }

    /**
     *
     * @Description: 使用Redisson框架来搞分布式锁
     *
     * @auther: 臧浩鹏
     * @date: 20:22 2018/8/1
     * @param: []
     * @return: void
     *
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderV4(){
        RLock lock = redissonManagerConfig.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK);
        boolean getlock = false;

        try {
            //wait time 应该为0 要预估作业执行时间
            if(getlock = lock.tryLock(2,5, TimeUnit.SECONDS)){
                log.info("Redisson获取到分布式锁:{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK,Thread.currentThread().getName());
                Integer hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
                iOrderService.closeOrder(hour);
                log.info("执行关单完毕");
            }else {
                log.info("Redisson没有获取到分布式锁:{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_LOCK_TASK,Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.error("Redisson获取分布式锁异常！");
        }finally {
            if(!getlock){
                return;
            }
            lock.unlock();
            log.info("Redisson释放分布式锁！");
        }
    }

}
