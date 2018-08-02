/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CheckSecurity
 * Author:   臧浩鹏
 * Date:     2018/7/25 8:03
 * Description: 封装检查的登录权限
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.common;

import com.zhp.model.User;
import com.zhp.service.IUserService;
import com.zhp.util.CookieUtil;
import com.zhp.util.JsonUtil;
import com.zhp.util.RedisSharderPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 〈一句话功能简述〉<br> 
 * 〈封装检查的登录权限〉
 *
 * @author 臧浩鹏
 * @create 2018/7/25
 * @since 1.0.0
 */
@Service
public class CheckSecurity {
    @Autowired
    private IUserService iUserService;

    /**
     *
     * @Description: 0->未登录；1->是管理；2->普通用户
     *
     * @auther: 臧浩鹏
     * @date: 20:12 2018/7/27
     * @param: [session]
     * @return: java.lang.Integer
     *
     */
    public Integer checkAdmin(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return 0;
        }
        ServerResponse<String> response = iUserService.checkIsAdmin(user);
        if(response.isSuccess()){
            return 1;
        }else {
            return 2;
        }

    }
}
