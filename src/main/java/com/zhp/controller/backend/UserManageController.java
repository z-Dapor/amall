/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: UserManageController
 * Author:   臧浩鹏
 * Date:     2018/7/24 13:04
 * Description: 管理员控制器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.backend;

import com.zhp.common.Const;
import com.zhp.common.ServerResponse;
import com.zhp.model.User;
import com.zhp.service.IUserService;
import com.zhp.util.CookieUtil;
import com.zhp.util.JsonUtil;
import com.zhp.util.RedisSharderPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 〈一句话功能简述〉<br> 
 * 〈后台管理员控制器〉
 *
 * @author 臧浩鹏
 * @create 2018/7/24
 * @since 1.0.0
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {
@Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse response){
        ServerResponse<User> login = iUserService.login(username, password);
        if (login.isSuccess()){
            if(login.getData().getRole()==Const.Role.ROLE_ADMIN){
                CookieUtil.writeLoginToken(response,session.getId());
                RedisSharderPoolUtil.setEx(session.getId(), JsonUtil.obj2String(login.getData()),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
                return login;
            }else {
                return ServerResponse.createByErrorMessage("404");
            }
        }
        return login;
    }
}
