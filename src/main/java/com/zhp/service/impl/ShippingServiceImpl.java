/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ShippingServiceImpl
 * Author:   臧浩鹏
 * Date:     2018/7/26 13:29
 * Description: 收货地址的实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhp.common.ServerResponse;
import com.zhp.mapper.ShippingMapper;
import com.zhp.model.Shipping;
import com.zhp.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈收货地址的实现类〉
 *
 * @author 臧浩鹏
 * @create 2018/7/26
 * @since 1.0.0
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;


    @Override
    public ServerResponse addShipping(Integer uid, Shipping shipping) {
        shipping.setUserId(uid);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount>0){
            HashMap map = Maps.newHashMap();
            map.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",map);
        }
        return ServerResponse.createByErrorMessage("创建地址失败，请重新尝试！");
    }

    @Override
    public ServerResponse<String> delShipping(Integer uid, Integer shippingId) {
        int res = shippingMapper.deleteByUidAndSid(uid,shippingId);
        if(res>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除失败，请过会重试");
    }

    @Override
    public ServerResponse updateShipping(Integer id, Shipping shipping) {
        shipping.setUserId(id);
        int res = shippingMapper.updateByShipping(shipping);
        if(res>0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("删除失败，请过会重试");
    }

    @Override
    public ServerResponse<Shipping> selectShipping(Integer id, Integer shippingId) {
        Shipping res = shippingMapper.selectByShipping(id, shippingId);
        if(res==null){
            return ServerResponse.createByErrorMessage("不存在改地址");
        }
        return ServerResponse.createBySuccess("查找成功",res);
    }

    @Override
    public ServerResponse<PageInfo> listShipping(Integer uid, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> res = shippingMapper.selectAll(uid);
        PageInfo<Shipping> pageInfo = new PageInfo<>(res);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
