/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: Const
 * Author:   臧浩鹏
 * Date:     2018/7/24 8:53
 * Description: 常量类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 〈一句话功能简述〉<br> 
 * 〈常量类〉
 *
 * @author 臧浩鹏
 * @create 2018/7/24
 * @since 1.0.0
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";
    public static final String USER_NAME = "username";
    public static final String EMAIL = "email";
    public static final String UN_LOGIN = "UN_LOGIN";
    public static final String COMMON_USER = "COMMON_USER";
    public static final String TOKEN = "token_";

    public interface RedisCacheExtime{
        //30分钟
        int REDIS_SESSION_EXTIME = 60 * 30;
    }
    public interface Role{
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    public interface Cart{
        int CHECKED = 1;
        int UN_CHECKED = 0;
        String LIMIT_NUM_FAILED = "LIMIT_NUM_FAILED";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }
    /**
     *
     * @Description: 订单状态枚举
     *
     * @auther: 臧浩鹏
     * @date: 10:55 2018/7/27
     * @param:
     * @return:
     *
     */
    public enum OrderStatusEnum{
        CANCELED(0,"已取消"),
        NO_PAY(10,"未支付"),
        PAID(20,"已支付"),
        SHIPPED(40,"已发货"),
        ORDER_SUCCESS(50,"订单完成"),
        ORDER_CLOSE(60,"订单关闭")
        ;

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private int code;
        private String value;

        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }

        public static OrderStatusEnum codeOf(int code){
            for (OrderStatusEnum orderStatusEnum : values()){
                if(code == orderStatusEnum.getCode()){
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public interface AlipayCallBack{
        String TRADE_STATUS_WAIT_BY_PAY = "WAIT_BY_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    public enum PayPlatformEnum{
        ALIPAY(1,"支付宝");
        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private int code;
        private String value;

        public int getCode() {
            return code;
        }
        public String getValue() {
            return value;
        }
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    public enum ProductStatusEnum{
        ON_SALE(1,"在线");
        private int code;
        private  String value;
        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }
    }

    public enum payMentTypeEnum{
        ONLINE_PAY(1,"在线支付");

        private int code;
        private  String value;
        payMentTypeEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static payMentTypeEnum codeOf(int code){
            for (payMentTypeEnum payMentTypeEnum : values()){
                if(code == payMentTypeEnum.getCode()){
                    return payMentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public interface REDIS_LOCK{
        //关闭订单的分布式锁
        String CLOSE_ORDER_LOCK_TASK = "CLOSE_ORDER_LOCK_TASK";
    }
}
