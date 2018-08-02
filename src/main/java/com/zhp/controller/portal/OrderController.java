/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: OrderController
 * Author:   臧浩鹏
 * Date:     2018/7/27 8:11
 * Description: 订单和支付的controller
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhp.common.Const;
import com.zhp.common.ResponseCode;
import com.zhp.common.ServerResponse;
import com.zhp.model.User;
import com.zhp.service.IOrderService;
import com.zhp.util.CookieUtil;
import com.zhp.util.JsonUtil;
import com.zhp.util.RedisSharderPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈订单和支付的controller〉
 *
 * @author 臧浩鹏
 * @create 2018/7/27
 * @since 1.0.0
 */
@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("/pay.do")
    @ResponseBody
    public ServerResponse payOrder(HttpServletRequest request,Integer OrderNo){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.pay(OrderNo,user.getId(),request.getServletContext().getRealPath("upload"));

    }

    @RequestMapping("/alipay_callback.do")
    @ResponseBody
    public Object alipayCallBack(HttpServletRequest request){
        HashMap<String, String> params = Maps.newHashMap();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for(Iterator iter = parameterMap.keySet().iterator();iter.hasNext();){
            String name = (String) iter.next();
            String[] values = parameterMap.get(name);
            String valueStr = "";
            for (int i = 0;i<values.length;i++){
                valueStr = (i==values.length-1)?valueStr+values[i]:valueStr+values[i]+",";
            }
            params.put(name,valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        //验证回调的正确性，是不是支付宝 发的，并且还要避免重复通知
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求，验证不通过，再来我可报警了！");
            }
        } catch (AlipayApiException e) {
            log.info("支付宝回调出现异常",e);
        }
        //todo

        ServerResponse response = iOrderService.aliCallBack(params);
        if(response.isSuccess()){
            return Const.AlipayCallBack.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallBack.RESPONSE_FAILED;
    }

    @RequestMapping("/query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest request,Integer OrderNo){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse response = iOrderService.queryOderPayStatus(OrderNo,user.getId());
        if (response.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
    
    /**
     *
     * @Description: 创建订单 获取即将下单的商品信息，订单列表，订单详情，取消订单，订单列表，订单搜索，订单发货
     * 
     * @auther: 臧浩鹏 
     * @date: 13:41 2018/7/27
     *
     */
    @RequestMapping("/create.do")
    @ResponseBody
    public ServerResponse createOrder(HttpServletRequest request,Integer shippingId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);

    }

    @RequestMapping("/cancel.do")
    @ResponseBody
    public ServerResponse cancelOrder(HttpServletRequest request,long orderNo){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.concelOrder(user.getId(),orderNo);
    }

    /**
     *
     * @Description: 用户只在购物车中勾选了部分商品进行了购买，此方法是获取并显示剩下未购买部分的购物车商品
     *
     * @auther: 臧浩鹏
     * @date: 19:24 2018/7/27
     * @param: [session, orderNo]
     * @return: com.zhp.common.ServerResponse
     *
     */
    @RequestMapping("/get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }

    @RequestMapping("/detail.do")
    @ResponseBody
    public ServerResponse getOrderDetail(HttpServletRequest request,long orderNo){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @RequestMapping("/list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getOrderList(HttpServletRequest request, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }


}
