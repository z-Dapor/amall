package com.zhp.mapper;

import com.zhp.model.Orderitem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderitemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Orderitem record);

    int insertSelective(Orderitem record);

    Orderitem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Orderitem record);

    int updateByPrimaryKey(Orderitem record);

    List<Orderitem> selectByOrderNo(@Param("orderNo") Long orderNo,@Param("userId") Integer userId);


    int batchInsert(@Param("orderItemList") List<Orderitem> orderItemList);

    List<Orderitem> selectAllByAdmin(Long orderNo);
}