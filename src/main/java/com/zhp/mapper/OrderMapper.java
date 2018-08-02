package com.zhp.mapper;

import com.zhp.model.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectOrderByONoAndUid(@Param("orderNo") long orderNo, @Param("uId") Integer uId);

    Order selectByOrderNO(long orderNo);

    int deleteByUidAndOrderNO(@Param("uid") Integer uid, @Param("orderNo") long orderNo);

    List<Order> selectOrderByUid(Integer uid);

    List<Order> listOrder();

    List<Order> selectOrderStatusByCreateTime(@Param("status") Integer status,@Param("date") String date);
}