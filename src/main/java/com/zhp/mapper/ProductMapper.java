package com.zhp.mapper;

import com.zhp.model.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectAllProduct();

    List<Product> selectByProductNameAndId(@Param("productName") String productName, @Param("productId") Integer productId);

    Product selectByCategoryId(Integer categoryId);

    List<Product> selectByProductName(String name);

    //考虑到商品已经删除

    Integer selectStockByProductId(Integer productId);
}