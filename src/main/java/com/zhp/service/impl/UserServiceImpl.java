/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: UserServiceImpl
 * Author:   臧浩鹏
 * Date:     2018/7/23 19:32
 * Description: 实现user接口
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.service.impl;

import com.zhp.common.Const;
import com.zhp.common.ServerResponse;
import com.zhp.mapper.UserMapper;
import com.zhp.model.User;
import com.zhp.service.IUserService;
import com.zhp.util.MD5utils;
import com.zhp.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 〈一句话功能简述〉<br> 
 * 〈实现user接口〉
 *
 * @author 臧浩鹏
 * @create 2018/7/23
 * @since 1.0.0
 */
@Service(value = "iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {
        int res = userMapper.checkUserName(username);
        if (res==0){
            return  ServerResponse.createByErrorMessage("用户不存在！");
        }
        String md5pass = MD5utils.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5pass);
        if(user==null){
            return  ServerResponse.createByErrorMessage("密码错误!");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }

    @Override
    public ServerResponse<String> register(User user){
        ServerResponse<String> usernamevalid = isValid(user.getUsername(), Const.USER_NAME);
        if(!usernamevalid.isSuccess()){
                return usernamevalid;
            }
        ServerResponse<String> emailvalid = isValid(user.getUsername(), Const.EMAIL);
        if(!emailvalid.isSuccess()){
            return emailvalid;
        }
            user.setPassword(MD5utils.MD5EncodeUtf8(user.getPassword()));
            user.setRole(Const.Role.ROLE_CUSTOMER);
        int insertcount = userMapper.insert(user);
        if (insertcount == 0){
            return ServerResponse.createByErrorMessage("注册失败!");
        }
        return  ServerResponse.createBySuccess("注册成功!");
    }

    @Override
    public ServerResponse<String> isValid(String str, String type){

        if(StringUtils.isNotBlank(str)){
            if (Const.USER_NAME.equals(type)){

                int i = userMapper.checkUserName(str);
                if(i>0){
                    return ServerResponse.createByErrorMessage("用户名已存在!");
                }
            }
            if(Const.EMAIL.equals(type)){
                int email = userMapper.checkEmail(type);
                if (email>0){
                    return  ServerResponse.createByErrorMessage("邮箱已存在!");
                }
            }
        }else {
            return ServerResponse.createByErrorMessage("参数错误！");
        }
        return ServerResponse.createBySuccess("检测通过!");
    }

    @Override
    public ServerResponse<String> getQuestion(String username){
        ServerResponse<String> valid = isValid(username, Const.USER_NAME);
        if (valid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        String question = userMapper.selectQuestion(username);
        return ServerResponse.createBySuccess(question);
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        ServerResponse<String> valid = isValid(username, Const.USER_NAME);
        if (valid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        int res = userMapper.checkQuestion(username, question, answer);
        if (res>0){
            String token = UUID.randomUUID().toString();
            RedisPoolUtil.setEx(Const.TOKEN+username,token,60*60);
            //TokenCache.set(TokenCache.TOKEN+username,token);
            return ServerResponse.createBySuccess(token);
        }
        return ServerResponse.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ServerResponse<String> forgetRestPassword(String username, String newpassword, String token){
        ServerResponse<String> valid = isValid(username, Const.USER_NAME);
        if (valid.isSuccess()){
            return ServerResponse.createByErrorMessage("用户不存在！");
        }
        if (StringUtils.equals(RedisPoolUtil.get(Const.TOKEN+username),token)&&token.length()>0){
            String md5 = MD5utils.MD5EncodeUtf8(newpassword);
            int res = userMapper.updatePasswordWithForget(username, md5);
            if (res>0){
                return ServerResponse.createBySuccess("更改密码成功!");
            }

        }else {
            return ServerResponse.createByErrorMessage("token失效，取消更改！");
        }
        return ServerResponse.createByErrorMessage("修改密码失败！");
    }

    @Override
    public ServerResponse<String> restPassword(String oldPassword, String newPassword, User user) {
        int res = userMapper.checkPassword(user.getId(), MD5utils.MD5EncodeUtf8(oldPassword));
        if (res==0){
            return ServerResponse.createByErrorMessage("密码错误！");
        }
        user.setPassword(MD5utils.MD5EncodeUtf8(newPassword));
        int count = userMapper.updateByPrimaryKey(user);
        if (count>0){
            return ServerResponse.createBySuccess("修改密码成功！");
        }
        return ServerResponse.createByErrorMessage("修改密码失败！");
    }

    @Override
    public ServerResponse<User> updateUserInfo(int curid, User user) {
        //username 是不能更新的
        //email 也要进行校验！
        String email = user.getEmail();
        int count = userMapper.checkEmailWithId(curid, email);
        if(count>0){
            return ServerResponse.createByErrorMessage("邮箱被占用，请更换！");
        }
        User updateUser = new User();
        updateUser.setId(curid);
        updateUser.setEmail(email);
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int res = userMapper.updateByPrimaryKeySelective(updateUser);
        if (res>0){
            return ServerResponse.createBySuccess("更新个人信息成功！",updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败！");
    }

    @Override
    public ServerResponse<User> getUserInformation(int uid){
        User user = userMapper.selectByPrimaryKey(uid);
        if (user!=null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("查找错误！");
    }

    @Override
    public ServerResponse<String> checkIsAdmin(User user) {
        if (user.getRole()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess("是管理员！");
        }
        return ServerResponse.createByErrorMessage("非管理员！");
    }

}
