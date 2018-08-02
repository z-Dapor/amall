/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ErrorInfo
 * Author:   臧浩鹏
 * Date:     2018/8/1 8:26
 * Description: 异常封装类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 〈一句话功能简述〉<br> 
 * 〈异常封装类〉
 *
 * @author 臧浩鹏
 * @create 2018/8/1
 * @since 1.0.0
 */
@Getter
@Setter
public class ErrorInfo<T> {
    public static final Integer OK = 0;
    public static final Integer ERROR = 100;

    private Integer code;
    private String message;
    private String url;
    private T data;
}
