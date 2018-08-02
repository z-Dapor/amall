package com.zhp.service;

import com.zhp.common.ServerResponse;
import com.zhp.vo.CartVo;

/**
 * Created by Administrator on 2018/7/26.
 */
public interface ICartService {
    ServerResponse<CartVo> addCart(Integer productId, Integer userId, Integer count);

    ServerResponse<CartVo> updateCart(Integer productId, Integer id, Integer count);

    ServerResponse<CartVo> deleteCart(String productIds, Integer id);

    ServerResponse<CartVo> list(Integer id);

    ServerResponse<CartVo> selectOrUnselectAll(Integer Uid, Integer checked,Integer productId);

    ServerResponse<Integer> getProductCount(Integer id);
}
