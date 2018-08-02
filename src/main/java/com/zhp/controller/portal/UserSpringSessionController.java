/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: UserController
 * Author:   臧浩鹏
 * Date:     2018/7/23 19:23
 * Description: 前台的用户控制
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.portal;

import com.zhp.common.Const;
import com.zhp.common.ServerResponse;
import com.zhp.model.User;
import com.zhp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 〈一句话功能简述〉<br> 
 * 〈前台的用户控制〉
 *
 * @author 臧浩鹏
 * @create 2018/7/23
 * @since 1.0.0
 */
@Controller
@RequestMapping("/user/session")
public class UserSpringSessionController {
    @Autowired
    private IUserService iUserService;


    @RequestMapping("/hello")
    public String hello() throws Exception {
        throw new Exception("发生错误");
    }



    @RequestMapping(value = "login.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse response){
        ServerResponse<User> login = iUserService.login(username, password);
        if (login.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,login.getData());
            System.out.println(login.getData()+"-----------------++++++++++++++++++++++");
            /*CookieUtil.writeLoginToken(response,session.getId());
            RedisSharderPoolUtil.setEx(session.getId(), JsonUtil.obj2String(login.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);*/
        }
        return login;
    }
    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    @ResponseBody
    public  ServerResponse<String> logout(HttpSession session,HttpServletRequest request,HttpServletResponse response){
        session.removeAttribute(Const.CURRENT_USER);
        /*String token = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request,response);
        RedisSharderPoolUtil.del(token);*/
        return ServerResponse.createBySuccess("退出成功!");
    }

    @RequestMapping(value = "getUserInfo.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request,HttpSession session){
       /* String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);*/
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
    }
}
