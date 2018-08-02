package com.zhp.service;

import com.github.pagehelper.PageInfo;
import com.zhp.common.ServerResponse;
import com.zhp.vo.OrderVo;

import java.util.Map;

/**
 *
 * @author 臧浩鹏
 * @date 2018/7/27
 */
public interface IOrderService {

    ServerResponse pay(long orderNo, Integer uId, String path);

    ServerResponse aliCallBack(Map<String,String> params);

    ServerResponse<Boolean> queryOderPayStatus(Integer orderNo, Integer id);

    ServerResponse createOrder(Integer id, Integer shippingId);

    ServerResponse concelOrder(Integer id, long orderNo);

    ServerResponse getOrderCartProduct(Integer id);

    ServerResponse getOrderDetail(Integer uid, long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer uid, int pageNum, int pageSize);

    ServerResponse<PageInfo> manageList(int pageNum, int pageSize);

    ServerResponse<OrderVo> manageorderDetail(long orderNo);

    ServerResponse<PageInfo> manageorderSearch(long orderNo, int pageNum, int pageSize);

    ServerResponse<String> orderSendGoods(long orderNo);


    //hour个小时未付款的订单进行关闭

    void closeOrder(int hour);

}
