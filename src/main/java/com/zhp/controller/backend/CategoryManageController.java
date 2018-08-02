/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: CategoryManageController
 * Author:   臧浩鹏
 * Date:     2018/7/24 16:40
 * Description: 分类管理controller
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.backend;

import com.zhp.common.ServerResponse;
import com.zhp.service.ICategoryService;
import com.zhp.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 〈一句话功能简述〉<br> 
 * 〈分类管理controller〉
 *
 * @author 臧浩鹏
 * @create 2018/7/24
 * @since 1.0.0
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest request, String categoryname, @RequestParam(value = "parentId",defaultValue = "0") int parentId){
        return iCategoryService.addCategory(categoryname,parentId);
        /*String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录！");
        }
        ServerResponse<String> response = iUserService.checkIsAdmin(user);
        if (response.isSuccess()){
            //是管理员

        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限！");

        }
        return response;*/
    }
    @RequestMapping(value = "set_categoryName.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setCategoryName(String categoryName,Integer categoryId,HttpServletRequest request){
        return iCategoryService.updateCategoryName(categoryId, categoryName);
        /*String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录！");
        }
        ServerResponse<String> res = iUserService.checkIsAdmin(user);
        if (res.isSuccess()) {
            ServerResponse response =
            return response;
        }else {
            return ServerResponse.createByErrorMessage("请求超权限");
        }*/
    }
    @RequestMapping(value = "get_categoryChild.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getChildParallelCategory(@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId,HttpServletRequest request){
        return  iCategoryService.getChildParallelCategory(categoryId);
        /*String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录！");
        }
        ServerResponse<String> res = iUserService.checkIsAdmin(user);
        if(res.isSuccess()&&categoryId!=null){
            //查询子节点的信息，紧当前子节点 无递归
        }else {
            return ServerResponse.createByErrorMessage("请求超权限");
        }*/
    }

    @RequestMapping(value = "get_deep_categoryChild.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getDeepChildCategory(@RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId,HttpServletRequest request){
        return  iCategoryService.selectChildCategoryIdWithDeep(categoryId);
        /*String token = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(token)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取当前用户信息，请登录！");
        }

        String userJsonStr = RedisSharderPoolUtil.get(token);
        User user = JsonUtil.str2Obj(userJsonStr,User.class);
        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"请登录！");
        }
        ServerResponse<String> res = iUserService.checkIsAdmin(user);
        if(res.isSuccess()&&categoryId!=null){
            //查询当前节点的id及其子节点
        }else {
            return ServerResponse.createByErrorMessage("请求超权限");
        }*/
    }
}
