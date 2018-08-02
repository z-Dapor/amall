package com.zhp.mapper;

import com.zhp.model.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUidAndSid(@Param("uid") Integer uid, @Param("shippingId") Integer shippingId);

    int updateByShipping(Shipping shipping);

    Shipping selectByShipping(@Param("uid") Integer uid, @Param("shippingId") Integer shippingId);

    List<Shipping> selectAll(Integer uid);
}