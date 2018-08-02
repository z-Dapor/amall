/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: GlobalExceptionHandler
 * Author:   臧浩鹏
 * Date:     2018/7/31 18:55
 * Description: 统一全局异常类、
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.common;

import com.zhp.common.ErrorInfo;
import com.zhp.common.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 〈一句话功能简述〉<br> 
 * 〈统一全局异常类、〉
 *
 * @author 臧浩鹏
 * @create 2018/7/31
 * @since 1.0.0
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    public static final String DEFAULT_ERROR_VIEW = "error";

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ErrorInfo defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        ErrorInfo errorInfo = new ErrorInfo();
        log.error("出现异常:{} exception ",req.getRequestURL(),e);
        errorInfo.setCode(ResponseCode.ERROR.getCode());
        errorInfo.setMessage("接口异常");
        errorInfo.setData(e.toString());
        errorInfo.setUrl(req.getRequestURL().toString());
        return errorInfo;
    }
}
