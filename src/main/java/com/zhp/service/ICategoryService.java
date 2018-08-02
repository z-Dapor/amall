package com.zhp.service;

import com.zhp.common.ServerResponse;
import com.zhp.model.Category;

import java.util.List;

/**
 * Created by Administrator on 2018/7/24.
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryname, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectChildCategoryIdWithDeep(Integer categoryId);
}
