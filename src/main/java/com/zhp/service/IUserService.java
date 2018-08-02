package com.zhp.service;

import com.zhp.common.ServerResponse;
import com.zhp.model.User;

/**
 * Created by 臧浩鹏 on 2018/7/23.
 */
public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> isValid(String str,String type);

    ServerResponse<String> getQuestion(String username);

    ServerResponse<String> checkAnswer(String username,String question,String answer);

    ServerResponse<String> forgetRestPassword(String username,String newpassword,String token);

    ServerResponse<String> restPassword(String oldPassword, String newPassword, User user);

    ServerResponse<User> updateUserInfo(int curid, User user);

    ServerResponse<User> getUserInformation(int uid);

    ServerResponse<String> checkIsAdmin(User user);
}
