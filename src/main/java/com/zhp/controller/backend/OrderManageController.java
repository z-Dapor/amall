/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: OrderManageController
 * Author:   臧浩鹏
 * Date:     2018/7/27 20:07
 * Description: 后台管理员的订单controller
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.backend;

import com.github.pagehelper.PageInfo;
import com.zhp.common.CheckSecurity;
import com.zhp.common.ServerResponse;
import com.zhp.service.IOrderService;
import com.zhp.vo.OrderVo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 〈一句话功能简述〉<br>
 * 〈后台管理员的订单controller〉
 *
 * @author 臧浩鹏
 * @create 2018/7/27
 * @since 1.0.0
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private CheckSecurity checkSecurity;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderList(HttpServletRequest request, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return iOrderService.manageList(pageNum,pageSize);
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res==1){
        }
        return res==0?ServerResponse.createByErrorMessage("未登录,请登录！"):ServerResponse.createByErrorMessage("无权限操作！");*/
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<OrderVo> orderDetail(HttpServletRequest request, long orderNo){
        return iOrderService.manageorderDetail(orderNo);
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res==1){
        }
        return res==0?ServerResponse.createByErrorMessage("未登录,请登录！"):ServerResponse.createByErrorMessage("无权限操作！");*/
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> orderSearch(HttpServletRequest request, long orderNo, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return iOrderService.manageorderSearch(orderNo,pageNum,pageSize);
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res==1){
        }
        return res==0?ServerResponse.createByErrorMessage("未登录,请登录！"):ServerResponse.createByErrorMessage("无权限操作！");*/
    }


    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse<String> orderSendGoods(HttpServletRequest request, long orderNo){
        return iOrderService.orderSendGoods(orderNo);
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res==1){
        }
        return res==0?ServerResponse.createByErrorMessage("未登录,请登录！"):ServerResponse.createByErrorMessage("无权限操作！");*/
    }
}
