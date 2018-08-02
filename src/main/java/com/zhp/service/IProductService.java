package com.zhp.service;

import com.github.pagehelper.PageInfo;
import com.zhp.common.ServerResponse;
import com.zhp.model.Product;
import com.zhp.vo.ProductDetailVo;

/**
 * Created by Administrator on 2018/7/25.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse UpdateSaleStatus(Integer id, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize);

    ServerResponse<ProductDetailVo> lookProductDeatil(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategoryId(String orderBy,String keyword,Integer categoryId,Integer pageNum, Integer pageSize );
}
