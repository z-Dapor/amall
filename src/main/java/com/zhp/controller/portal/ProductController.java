/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ProductController
 * Author:   臧浩鹏
 * Date:     2018/7/25 15:07
 * Description: 前台商品controller层
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.portal;

import com.zhp.common.ServerResponse;
import com.zhp.service.IProductService;
import com.zhp.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈前台商品controller层〉
 *
 * @author 臧浩鹏
 * @create 2018/7/25
 * @since 1.0.0
 */
@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse productDetail(Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorMessage("请输入正确的产品ID");
        }
        ServerResponse<ProductDetailVo> response = iProductService.lookProductDeatil(productId);
        return response;
    }

    @RequestMapping(value = "/{productId}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse detailResful(@PathVariable Integer productId){
        if(productId==null){
            return ServerResponse.createByErrorMessage("请输入正确的产品ID");
        }
        ServerResponse<ProductDetailVo> response = iProductService.lookProductDeatil(productId);
        return response;
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "keyword",required = false) String keyword,
                               @RequestParam(value = "categoryId",required = false) Integer categoryId,
                               @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "orderBy",defaultValue = "") String orderBy){

    return iProductService.getProductByKeywordCategoryId(orderBy,keyword,categoryId,pageNum,pageSize);
    }

    //占位符

    @RequestMapping(value = "{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse listResful(@RequestParam(value = "keyword",required = false) String keyword,
                               @RequestParam(value = "categoryId",required = false) Integer categoryId,
                               @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "orderBy",defaultValue = "") String orderBy){

        return iProductService.getProductByKeywordCategoryId(orderBy,keyword,categoryId,pageNum,pageSize);
    }


}
