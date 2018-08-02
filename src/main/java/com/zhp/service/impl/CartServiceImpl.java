/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CartServiceImpl
 * Author:   臧浩鹏
 * Date:     2018/7/26 8:27
 * Description: 购物车的实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.zhp.common.Const;
import com.zhp.common.ResponseCode;
import com.zhp.common.ServerResponse;
import com.zhp.mapper.CartMapper;
import com.zhp.mapper.ProductMapper;
import com.zhp.model.Cart;
import com.zhp.model.Product;
import com.zhp.service.ICartService;
import com.zhp.util.BigDecimalUtil;
import com.zhp.util.PropertiesUtil;
import com.zhp.vo.CartProductVo;
import com.zhp.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈购物车的实现类〉
 *
 * @author 臧浩鹏
 * @create 2018/7/26
 * @since 1.0.0
 */
@Service(value = "iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> addCart(Integer productId, Integer userId, Integer count){

        if(productId==null||count==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectBypIdAnduId(productId, userId);
        if(cart==null){
            Cart icart = new Cart();
            icart.setProductId(productId);
            icart.setQuantity(count);
            icart.setChecked(Const.Cart.CHECKED);
            icart.setUserId(userId);
            cartMapper.insert(icart);
        }else {
            cart.setQuantity(cart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> updateCart(Integer productId, Integer userId, Integer count) {
        if(productId==null||count==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectBypIdAnduId(productId, userId);
        if(cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> deleteCart(String productIds, Integer userId) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUidAndPids(productList,userId);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> list(Integer id) {
        CartVo cartVo = getCartVoLimit(id);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnselectAll(Integer userId, Integer productId,Integer checked) {
        cartMapper.CheckOrUnCheck(userId,productId,checked);
        return this.list(userId);
    }

    @Override
    public ServerResponse<Integer> getProductCount(Integer uid) {
        if(uid==null){
            return ServerResponse.createBySuccess(0);
        }
        return  ServerResponse.createBySuccess(cartMapper.selectCartProductCount(uid));
    }

    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> carts = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(carts)){
            for(Cart cart : carts){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setProductId(cart.getProductId());
                cartProductVo.setUserId(cart.getUserId());

                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product!=null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductStock(product.getStock());

                    int buyLimitCount = 0;
                    if(product.getStock()>=cart.getQuantity()){
                        //库存充足时
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        //库存不足
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAILED);
                        Cart newcCart = new Cart();
                        newcCart.setId(cart.getId());
                        newcCart.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(newcCart);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(cartProductVo.getQuantity().doubleValue(),cartProductVo.getProductPrice().doubleValue()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                if(cart.getChecked()==Const.Cart.CHECKED){
                    //如果已经勾选，增加到整个购物车的总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
            cartVo.setCartTotalPrice(cartTotalPrice);
            cartVo.setCartProductVoList(cartProductVoList);
            cartVo.setAllChecked(this.getAllCheckedStatus(userId));
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private Boolean getAllCheckedStatus(Integer userId) {
        if(userId == null){
            return false;
        }
        //如果为零 说明没有查到没有选中的cart 则证明全选
        return cartMapper.SelectCartProductCheckedStatusByUid(userId) == 0;
    }

}
