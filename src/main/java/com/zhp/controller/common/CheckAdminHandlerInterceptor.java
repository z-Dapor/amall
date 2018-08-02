/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CheckAdminHandlerInterceptor
 * Author:   臧浩鹏
 * Date:     2018/8/1 9:38
 * Description: 实现验证管理员的拦截器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.common;

import com.google.common.collect.Maps;
import com.zhp.common.Const;
import com.zhp.common.ServerResponse;
import com.zhp.model.User;
import com.zhp.util.CookieUtil;
import com.zhp.util.JsonUtil;
import com.zhp.util.RedisSharderPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈实现验证管理员的拦截器〉
 *
 * @author 臧浩鹏
 * @create 2018/8/1
 * @since 1.0.0
 */
@Slf4j
@Component
public class CheckAdminHandlerInterceptor implements HandlerInterceptor{
    /**
     *
     * @Description: 拦截器分为 1.在controller调用前执行；2.在controller 执行之后，且页面渲染之前调用 3.页面渲染之后调用，一般用于资源清理操作
     *               返回 true则继续执行 ，否则取消执行
     * @auther: 臧浩鹏
     * @date: 9:47 2018/8/1
     * @param: [request, response, handler]
     * @return: boolean
     *
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod method = (HandlerMethod) handler;
        String methodName = method.getMethod().getName();
        String className = method.getBean().getClass().getSimpleName();

        User user=null;
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isNotEmpty(token)){
            String userJsonStr = RedisSharderPoolUtil.get(token);
            user = JsonUtil.str2Obj(userJsonStr,User.class);
        }
        if(user==null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)){
            //此时要添加reset 否则会报异常 getWriter() has already been called for this response
            response.reset();
            //这里要设置编码，否则会乱码
            response.setCharacterEncoding("UTF-8");
            //设置返回值类型 因为全是json接口
            response.setContentType("application/json;charset=UTF-8");

            PrintWriter writer = response.getWriter();

            if (user == null){
                //由于富文本文件要求 要有对应的返回结果
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"uploadWithRichText")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "未登录！");
                    writer.print(JsonUtil.obj2String(resultMap));
                }else {
                    writer.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录！")));
                }
            }else {
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"uploadWithRichText")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限！");
                    writer.print(JsonUtil.obj2String(resultMap));
                }else {
                    writer.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户非管理员！")));
                }
            }
            writer.flush();
            writer.close();

            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
