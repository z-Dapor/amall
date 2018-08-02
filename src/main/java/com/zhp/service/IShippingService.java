package com.zhp.service;

import com.github.pagehelper.PageInfo;
import com.zhp.common.ServerResponse;
import com.zhp.model.Shipping;

/**
 * Created by Administrator on 2018/7/26.
 */
public interface IShippingService {
    ServerResponse addShipping(Integer id, Shipping shipping);

    ServerResponse<String> delShipping(Integer id, Integer shippingId);

    ServerResponse updateShipping(Integer id, Shipping shipping);

    ServerResponse<Shipping> selectShipping(Integer id, Integer shippingId);

    ServerResponse<PageInfo> listShipping(Integer id, int pageNum, int pageSize);
}
