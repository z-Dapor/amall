/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: SessionExpireFilter
 * Author:   臧浩鹏
 * Date:     2018/7/30 18:30
 * Description: 通过过滤器实现重置Redis的会话时间
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.common;

import com.zhp.common.Const;
import com.zhp.model.User;
import com.zhp.util.CookieUtil;
import com.zhp.util.JsonUtil;
import com.zhp.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈通过过滤器实现重置Redis的会话时间〉
 *
 * @author 臧浩鹏
 * @create 2018/7/30
 * @since 1.0.0
 */
public class SessionExpireFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)servletRequest;
        String token = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(token)){
            String userJsonStr = RedisPoolUtil.get(token);
            User user = JsonUtil.str2Obj(userJsonStr, User.class);
            if(user!=null){
                RedisPoolUtil.setEx(token,JsonUtil.obj2String(user), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
