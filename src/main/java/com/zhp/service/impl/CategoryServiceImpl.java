/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CategoryServiceImpl
 * Author:   臧浩鹏
 * Date:     2018/7/24 16:58
 * Description: category的实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zhp.common.ServerResponse;
import com.zhp.mapper.CategoryMapper;
import com.zhp.model.Category;
import com.zhp.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br> 
 * 〈category的实现类〉
 *
 * @author 臧浩鹏
 * @create 2018/7/24
 * @since 1.0.0
 */
@Service(value = "iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService{
    //private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryname, Integer parentId){
        if (StringUtils.isBlank(categoryname)||parentId==null){
            return ServerResponse.createByErrorMessage("请添加匹配参数！");
        }
        Category category = new Category();
        category.setName(categoryname);
        category.setStatus(true);
        category.setParentId(parentId);
        int insert = categoryMapper.insert(category);
        if (insert>0){
            return ServerResponse.createBySuccess("增加品类成功！");
        }
        return ServerResponse.createByErrorMessage("添加品类失败！");


    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId==null||StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("请输入正确的参数!");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int i = categoryMapper.updateByPrimaryKeySelective(category);
        if(i>0){
            return  ServerResponse.createBySuccess("更改商品名称成功");
        }else {
            return ServerResponse.createByErrorMessage("更改失败！");
        }
    }

    @Override
    public ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId){
       List<Category> list = categoryMapper.getChildCategoryParallel(categoryId);
       if (CollectionUtils.isEmpty(list)){
            log.info("未找到当前分类的子分类");
       }
       return ServerResponse.createBySuccess(list);
    }
    /**
     *
     * @Description: 递归查询本节点的ID以及孩子的ID
     *
     * @auther: 臧浩鹏
     * @date: 19:23 2018/7/24
     * @param: [categoryId]
     * @return: com.zhp.common.ServerResponse
     *
     */
    @Override
    public ServerResponse<List<Integer>> selectChildCategoryIdWithDeep(Integer categoryId) {
        HashSet<Category> set = Sets.newHashSet();
        findChildCategory(set,categoryId);
        ArrayList<Integer> res = Lists.newArrayList();
        if(categoryId!=null) {
            for (Category ele : set) {
                res.add(ele.getId());
            }
        }
        return ServerResponse.createBySuccess(res);
    }

    public Set<Category> findChildCategory(Set<Category> set,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            set.add(category);
        }
        List<Category> childParallelCategory = categoryMapper.getChildCategoryParallel(categoryId);
        for(Category item:childParallelCategory){
                findChildCategory(set,item.getId());
        }
        return set;
    }

}
