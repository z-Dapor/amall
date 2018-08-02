package com.zhp.mapper;

import com.zhp.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    int checkEmail(String email);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    String selectQuestion(String username);

    int checkQuestion(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePasswordWithForget(@Param("username") String username, @Param("password") String password);

    int checkPassword(@Param("id") Integer id, @Param("password") String oldPassword);

    int checkEmailWithId(@Param("id") int curid,@Param("email") String email);

}