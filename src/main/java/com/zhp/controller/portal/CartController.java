/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CartController
 * Author:   臧浩鹏
 * Date:     2018/7/26 8:22
 * Description: 购物车的controller
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.portal;

import com.zhp.common.Const;
import com.zhp.common.ResponseCode;
import com.zhp.common.ServerResponse;
import com.zhp.model.User;
import com.zhp.service.ICartService;
import com.zhp.util.CookieUtil;
import com.zhp.util.JsonUtil;
import com.zhp.util.RedisSharderPoolUtil;
import com.zhp.vo.CartVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 〈一句话功能简述〉<br> 
 * 〈购物车的controller〉
 *
 * @author 臧浩鹏
 * @create 2018/7/26
 * @since 1.0.0
 */
@Controller()
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVo> addCart(HttpServletRequest request, Integer productId, Integer count){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<CartVo> response = iCartService.addCart(productId,user.getId(),count);
        return response;
    }
    @RequestMapping("update.do")
    @ResponseBody
    public  ServerResponse<CartVo> updateCart(HttpServletRequest request,Integer productId,Integer count){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<CartVo> response = iCartService.updateCart(productId,user.getId(),count);
        return response;
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public  ServerResponse<CartVo> deleteCart(HttpServletRequest request,String productIds){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<CartVo> response = iCartService.deleteCart(productIds,user.getId());
        return response;
    }

    @RequestMapping("list.do")
    @ResponseBody
    public  ServerResponse<CartVo> listCart(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<CartVo> response =iCartService.list(user.getId());
        return response;

    }


    /**
     *
     * @Description: //全选
                     //全反选
                     //单独选
                     //单独反选
     *      封装一个方法 调用时只需要传一个标志
     * @auther: 臧浩鹏
     * @date: 10:49 2018/7/26
     * @param:
     * @return:
     *
     */

    @RequestMapping("select_All.do")
    @ResponseBody
    public  ServerResponse<CartVo> selectAll(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<CartVo> response = iCartService.selectOrUnselectAll(user.getId(),Const.Cart.CHECKED,null);
        return response;
    }

    @RequestMapping("un_select_All.do")
    @ResponseBody
    public  ServerResponse<CartVo> unSelectAll(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<CartVo> response = iCartService.selectOrUnselectAll(user.getId(),Const.Cart.UN_CHECKED,null);
        return response;
    }

    @RequestMapping("select_one.do")
    @ResponseBody
    public  ServerResponse<CartVo> selectOne(HttpServletRequest request,Integer productId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<CartVo> response = iCartService.selectOrUnselectAll(user.getId(),Const.Cart.CHECKED,productId);
        return response;
    }

    @RequestMapping("unselect_one.do")
    @ResponseBody
    public  ServerResponse<CartVo> unSelectOne(HttpServletRequest request,Integer productId){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse<CartVo> response = iCartService.selectOrUnselectAll(user.getId(),Const.Cart.UN_CHECKED,productId);
        return response;
    }


    //查询当前用户 购物车的产品数量 ，如果一个产品有10个 那么数量就是10

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public  ServerResponse<Integer> getCartProductCount(HttpServletRequest request){
        String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.createBySuccess(0);
        }
        ServerResponse<Integer> response = iCartService.getProductCount(user.getId());
        return response;
    }

}
