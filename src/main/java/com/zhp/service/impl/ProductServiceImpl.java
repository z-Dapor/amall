/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ProductServiceImpl
 * Author:   臧浩鹏
 * Date:     2018/7/25 8:27
 * Description: Product接口的实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.zhp.common.Const;
import com.zhp.common.ResponseCode;
import com.zhp.common.ServerResponse;
import com.zhp.mapper.CategoryMapper;
import com.zhp.mapper.ProductMapper;
import com.zhp.model.Category;
import com.zhp.model.Product;
import com.zhp.service.ICategoryService;
import com.zhp.service.IProductService;
import com.zhp.util.DateTimeUtil;
import com.zhp.util.PropertiesUtil;
import com.zhp.vo.ProductDetailVo;
import com.zhp.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈Product接口的实现类〉
 *
 * @author 臧浩鹏
 * @create 2018/7/25
 * @since 1.0.0
 */
@Service(value = "iProductService")
public class ProductServiceImpl implements IProductService {


    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;
    @Override
    public ServerResponse saveOrUpdateProduct(Product product){
        System.out.println(product);
        if(product!=null){
            if(StringUtils.isNotBlank(product.getSubImages())) {
                String[] split = product.getSubImages().split(",");
                if (split.length > 0) {
                    product.setMainImage(split[0]);
                }
            }
                if(product.getId()!=null){
                    int res = productMapper.updateByPrimaryKeySelective(product);
                    if(res>0){
                        return ServerResponse.createBySuccess("更新商品成功");
                    }

                }else {
                    int res = productMapper.insert(product);
                    if (res>0){
                        return ServerResponse.createBySuccess("添加商品成功");
                    }
                }
        }else {
            return ServerResponse.createByErrorMessage("未传递商品参数！");
        }
        return ServerResponse.createByErrorMessage("操作失败！");
    }

    @Override
    public ServerResponse<String> UpdateSaleStatus(Integer id, Integer status) {
        if (id==null||status==null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(id);
        product.setStatus(status);
        int res = productMapper.updateByPrimaryKeySelective(product);
        if (res>0){
            return ServerResponse.createBySuccess("更新状态成功！");
        }
        return ServerResponse.createByErrorMessage("更新失败！");
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null){
            return ServerResponse.createByErrorMessage("请填写商品ID");
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("商品已下架");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        //startPage--start
        //填充sql语句
        //以pagehelper结尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> res = productMapper.selectAllProduct();
        ArrayList<ProductListVo> ProductListVo = Lists.newArrayList();
        for (Product pro:res
             ) {
            ProductListVo.add(assembleProductListVo(pro));
        }
        PageInfo<com.zhp.vo.ProductListVo> productListVoPageInfo = new PageInfo<>(ProductListVo);
        return ServerResponse.createBySuccess(productListVoPageInfo);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        //startPage--start
        //填充sql语句
        //以pagehelper结尾
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }

        List<Product> res = productMapper.selectByProductNameAndId(productName,productId);
        ArrayList<ProductListVo> ProductListVo = Lists.newArrayList();
        for (Product pro:res
                ) {
            ProductListVo.add(assembleProductListVo(pro));
        }
        PageInfo<com.zhp.vo.ProductListVo> productListVoPageInfo = new PageInfo<>(ProductListVo);
        return ServerResponse.createBySuccess(productListVoPageInfo);



    }

    @Override
    public ServerResponse<ProductDetailVo> lookProductDeatil(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product.getStatus()== Const.ProductStatusEnum.ON_SALE.getCode()){
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ServerResponse.createBySuccess(productDetailVo);
        }
        return ServerResponse.createByErrorMessage("商品已下架！");
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategoryId(String orderBy, String keyword, Integer categoryId, Integer pageNum, Integer pageSize ){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] split = orderBy.split("_");
                PageHelper.orderBy(split[0]+" "+split[1]);
            }
        }
        if(categoryId!=null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                List<ProductDetailVo> list = new ArrayList<>();
                PageInfo pageInfo = new PageInfo(list);
                return ServerResponse.createBySuccess(pageInfo);
            } else if (category != null) {
                    ServerResponse<List<Integer>> listServerResponse = iCategoryService.selectChildCategoryIdWithDeep(categoryId);
                    ArrayList<ProductListVo> res = Lists.newArrayList();
                    if (StringUtils.isBlank(keyword)) {
                        for (Integer category_Id : listServerResponse.getData()) {
                            Product product = productMapper.selectByCategoryId(category_Id);
                            if(product.getStatus()==Const.ProductStatusEnum.ON_SALE.getCode()){
                                res.add(assembleProductListVo(product));
                            }
                        }
                    }else {
                        for (Integer category_Id : listServerResponse.getData()) {
                            Product product = productMapper.selectByCategoryId(category_Id);
                            if (product.getStatus()==Const.ProductStatusEnum.ON_SALE.getCode()&&product.getName().contains(keyword)){
                                res.add(assembleProductListVo(product));
                            }
                        }
                    }
                    PageInfo pageInfo = new PageInfo(res);
                    return ServerResponse.createBySuccess(pageInfo);
            }
        }else if(StringUtils.isNotBlank(keyword)){
            String name = new StringBuilder().append("%").append(keyword).append("%").toString();
            List<Product> temp = productMapper.selectByProductName(name);
            List<ProductListVo> res = Lists.newArrayList();
            for(Product product:temp){
                if(product.getStatus()==Const.ProductStatusEnum.ON_SALE.getCode()) {
                    res.add(assembleProductListVo(product));
                }
            }
            PageInfo pageInfo = new PageInfo(res);
            return ServerResponse.createBySuccess(pageInfo);
        }
        return ServerResponse.createByErrorMessage("参数错误，请输入关键字或产品类别！");
    }

    /**
     *
     * @Description: 将pojo 转换为vo(value object)
     *
     * @auther: 臧浩鹏
     * @date: 10:11 2018/7/25
     * @param: [product]
     * @return: com.zhp.vo.ProductDetailVo
     *
     */
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.amall.com/"));

        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            //默认根节点
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }


    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

}
