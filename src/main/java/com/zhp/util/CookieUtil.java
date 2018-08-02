/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CookieUtil
 * Author:   臧浩鹏
 * Date:     2018/7/30 16:18
 * Description: Cookie的工具类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 〈一句话功能简述〉<br> 
 * 〈Cookie的工具类〉
 *
 * @author 臧浩鹏
 * @create 2018/7/30
 * @since 1.0.0
 */@Slf4j
public class CookieUtil {

    private final static String COOKIE_DOMAIN = "amall.com";
    private final static String COOKIE_NAME = "amall_login_token";

    public static String readLoginToken(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie : cookies){
                log.info("read cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    log.info("return cookieName:{},cookieValue:{}",cookie.getName(),cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void writeLoginToken(HttpServletResponse response,String token){
        Cookie cookie = new Cookie(COOKIE_NAME,token);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setPath("/");
        //防止通过脚本获得cookie信息 可提高安全性
        cookie.setHttpOnly(true);
        //如果是 -1 则永久；单位是 秒；如果maxage不设置的话，cookie不会写入硬盘，而是写在内存
        //只在当前页面有效
        cookie.setMaxAge(60 * 60 * 24 * 365);
        log.info("write cookieName:{},cookieValue",cookie.getName(),cookie.getValue());
        response.addCookie(cookie);

    }

    public static void
    delLoginToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie : cookies){
                if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                    cookie.setDomain(COOKIE_DOMAIN);
                    cookie.setPath("/");
                    //设置有效期为0
                    cookie.setMaxAge(0);
                    log.info("del cookieName:{},cookieValue",cookie.getName(),cookie.getValue());
                    response.addCookie(cookie);
                }
            }
        }
    }

}
