package com.zhp.mapper;

import com.zhp.model.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectBypIdAnduId(@Param("productId") Integer productId, @Param("userId") Integer userId);

    List<Cart> selectCartByUserId(Integer userId);

    int SelectCartProductCheckedStatusByUid(Integer userId);

    int deleteByUidAndPids(@Param("productList") List<String> productList, @Param("userId") Integer userId);

    void  CheckOrUnCheck(@Param("userId")Integer userId, @Param("checked")Integer checked,@Param("productId") Integer productId);

    int selectCartProductCount(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer uId);
}