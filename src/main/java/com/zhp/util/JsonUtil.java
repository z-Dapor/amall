/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: JsonUtil
 * Author:   臧浩鹏
 * Date:     2018/7/30 13:31
 * Description: 封装序列化反序列化方法
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.util;

import com.zhp.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 〈一句话功能简述〉<br> 
 * 〈封装序列化反序列化方法〉
 *
 * @author 臧浩鹏
 * @create 2018/7/30
 * @since 1.0.0
 */
@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        //对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        //取消默认转换timestamps形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATE_KEYS_AS_TIMESTAMPS,false);
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //将所有的日期格式都统一为以下的格式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
        //忽略 在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String obj2String(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse obj to string error",e);
            return null;
        }
    }
    /**
     *
     * @Description: 漂亮的序列化 格式化好的
     *
     * @auther: 臧浩鹏
     * @date: 13:47 2018/7/30
     * @param: [obj]
     * @return: java.lang.String
     *
     */
    public static <T> String obj2StringPretty(T obj){
        if(obj == null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse obj to string error",e);
            return null;
        }
    }

    public static <T> T str2Obj(String str,Class<T> clazz){
        if(StringUtils.isEmpty(str) || clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class)?(T)str:objectMapper.readValue(str,clazz);
        } catch (IOException e) {
            log.warn("Parse str to Obj error",e);
            return null;
        }
    }

    public static <T> T str2Obj(String str,TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(String.class) ? str :objectMapper.readValue(str,typeReference));
        } catch (IOException e) {
            log.warn("Parse str to Obj error",e);
            return null;
        }
    }

    public static <T> T str2Obj(String str,Class<?> collection,Class<?>... elementClasses){
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collection, elementClasses);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            log.warn("Parse str to Obj error",e);
            return null;
        }
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(10);
        user.setUsername("dapor");
        user.setEmail("515944701@qq.com");
        user.setCreateTime(new Date());
        log.info(user.toString());
       /* User user2 = new User();
        user2.setId(11);
        user2.setUsername("dapor");
        user2.setEmail("515944701@qq.com");

        List<User> users = Lists.newArrayList();
        users.add(user);
        users.add(user2);

        String s = JsonUtil.obj2String(users);

        List<User> list = JsonUtil.str2Obj(s, new TypeReference<List<User>>() {
        });
        List<User> list2= JsonUtil.str2Obj(s, List.class,User.class);
        log.info(list2.toString());*/


    }

}
