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
import com.zhp.common.ResponseCode;
import com.zhp.common.ServerResponse;
import com.zhp.model.User;
import com.zhp.service.IUserService;
import com.zhp.util.CookieUtil;
import com.zhp.util.JsonUtil;
import com.zhp.util.RedisSharderPoolUtil;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse response){
        ServerResponse<User> login = iUserService.login(username, password);
        if (login.isSuccess()){
            //session.setAttribute(Const.CURRENT_USER,login.getData());
            CookieUtil.writeLoginToken(response,session.getId());
            RedisSharderPoolUtil.setEx(session.getId(), JsonUtil.obj2String(login.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return login;
    }
    @RequestMapping(value = "logout.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> logout(HttpSession session,HttpServletRequest request,HttpServletResponse response){
        //session.removeAttribute(Const.CURRENT_USER);
        String token = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request,response);
        RedisSharderPoolUtil.del(token);
        return ServerResponse.createBySuccess("退出成功!");
    }
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServerResponse<String> register(User user){
        return iUserService.register(user);
    }
    @RequestMapping(value = "checkVaild.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkVaild(String str,String type){
        return iUserService.isValid(str, type);
    }
    @RequestMapping(value = "getUserInfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
    }
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
            return iUserService.getQuestion(username);
    }
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer){
            return iUserService.checkAnswer(username, question, answer);
    }
    @RequestMapping(value = "forget_Rest_Password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetRestPassword(String username,String newpassword,String token){
            return iUserService.forgetRestPassword(username,newpassword,token);
    }
    @RequestMapping(value = "rest_Password.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> RestPassword(HttpServletRequest request,String oldPassword,String newPassword){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户登录失效，请重新登录！");
        }
        return iUserService.restPassword(oldPassword,newPassword,user);
    }
    @RequestMapping(value = "update_Information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserinfo(HttpServletRequest request,User user){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User currentuser = JsonUtil.str2Obj(userJsonStr,User.class);
        if(currentuser==null){
            return ServerResponse.createByErrorMessage("用户未登录！请登录！");
        }
        int curid = currentuser.getId();
        user.setUsername(currentuser.getUsername());
        ServerResponse<User> serverResponse = iUserService.updateUserInfo(curid, user);
        if(serverResponse.isSuccess()){
            serverResponse.getData().setUsername(currentuser.getUsername());
            RedisSharderPoolUtil.setEx(token, JsonUtil.obj2String(serverResponse.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return serverResponse;
    }
    @RequestMapping(value = "get_Information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User currentUser = JsonUtil.str2Obj(userJsonStr,User.class);
        if (currentUser==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"需要登录！即将跳转到登录页面！");
        }
        Integer currentuserId = currentUser.getId();
        return iUserService.getUserInformation(currentuserId);
    }
}
