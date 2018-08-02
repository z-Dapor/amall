/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ResponseCode
 * Author:   臧浩鹏
 * Date:     2018/7/23 19:48
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.common;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author 臧浩鹏
 * @create 2018/7/23
 * @since 1.0.0
 */
public enum  ResponseCode {
    /**
     *
     * @Description: 
     * 
     * @auther: 臧浩鹏 
     * @date: 19:53 2018/7/23 
     * @param: 成功为0，错误为1，需要denglu为10，错误的参数为2
     * @return: 
     *
     */
    SUCCESS(0,"SUCCESS"),ERROR(1,"ERROR"),NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");


    private final int code;

    private final String desc;

    ResponseCode(int code,String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
