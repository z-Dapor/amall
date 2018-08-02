/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: ProductManageController
 * Author:   臧浩鹏
 * Date:     2018/7/25 8:11
 * Description: 后台商品管理
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zhp.common.CheckSecurity;
import com.zhp.common.ServerResponse;
import com.zhp.model.Product;
import com.zhp.service.IFileService;
import com.zhp.service.IProductService;
import com.zhp.util.PropertiesUtil;
import com.zhp.vo.ProductDetailVo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈后台商品管理〉
 *
 * @author 臧浩鹏
 * @create 2018/7/25
 * @since 1.0.0
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

@Autowired
    private IProductService iProductService;
@Autowired
    private IFileService iFileService;
@Autowired
    private CheckSecurity checkSecurity;
    /**
     * 获取file.html页面
     */
    @RequestMapping("/file")
    public String file(){
        return "upload";
    }

    @RequestMapping(value = "save.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse produceSave(HttpServletRequest request, Product product){
        return iProductService.saveOrUpdateProduct(product);
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res==1){
        }else {
            if(res == 0){
                return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
        }
        return ServerResponse.createByErrorMessage("请出去...");*/
    }

    @RequestMapping(value = "set_sale_status.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest request, Integer id,Integer status){
        return iProductService.UpdateSaleStatus(id,status);
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res==1){
        }else {
            if(res == 0){
                return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
        }
        return ServerResponse.createByErrorMessage("请出去...");*/
    }

    @RequestMapping(value = "detail.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(HttpServletRequest request, Integer productId){
        return iProductService.manageProductDetail(productId);
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res==1){
        }else {
            if(res == 0){
                return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
        }
        return ServerResponse.createByErrorMessage("请出去...");*/
    }

    @RequestMapping(value = "list.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpServletRequest request, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return iProductService.getProductList(pageNum,pageSize);
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res==1){
        }else {
            if(res == 0){
                return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
        }
        return ServerResponse.createByErrorMessage("请出去...");*/
    }

    @RequestMapping(value = "search.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(HttpServletRequest request,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res==1){
        }else {
            if(res == 0){
                return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
        }
        return ServerResponse.createByErrorMessage("请出去...");*/
    }


    @RequestMapping(value = "upload.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(@RequestParam("fileName") MultipartFile file, HttpServletRequest request){
        String path = request.getSession().getServletContext().getRealPath("upload");
        System.out.println(path+"=========================");
        if(file.isEmpty()){
            System.out.println("空文件");
            return ServerResponse.createByErrorMessage("空文件！");
        }
        ServerResponse response = iFileService.upload(file, path);

        if(response.isSuccess()){
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+response.getData();
            Map ress = Maps.newHashMap();
            ress.put("url",url);
            ress.put("uri",response.getData());
            return ServerResponse.createBySuccess(ress);
        }
        return response;
        /*Integer res = checkSecurity.checkAdmin(request);
        if(res!=1){
            return ServerResponse.createByErrorMessage("无权限或者还未登录！");
        }*/
    }

    @RequestMapping(value = "rich_text_upload.do",method = RequestMethod.POST)
    @ResponseBody
    public Map uploadWithRichText(@RequestParam("fileName") MultipartFile file, HttpServletRequest request,HttpServletResponse resp) {
        Map resultMap = Maps.newHashMap();
        /*Integer res = checkSecurity.checkAdmin(request);
        if (res != 1) {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限或者还未登录！");
            return resultMap;
        }*/
        //富文本中对于返回值有自己的要求 此时用的是 simiditor 所以按照simiditor的返回值返回！
       /* {
            "success":true or false,
            "msg":"error" or "other options"
            "file_path": "[real file path]"
        }*/

        String path = request.getSession().getServletContext().getRealPath("upload");
        System.out.println(path + "=========================");
        if (file.isEmpty()) {
            resultMap.put("success", false);
            resultMap.put("msg", "请选择文件上传！！！");
            return resultMap;
        }
        ServerResponse response = iFileService.upload(file, path);

        if (response.isSuccess()) {
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + response.getData();
            resultMap.put("success", true);
            resultMap.put("msg", "上传成功！");
            resultMap.put("file_path", url);

            resp.addHeader("Access-Control-Allow-Headers","x-File-Name");
            return resultMap;
        } else {
            resultMap.put("success",false);
            resultMap.put("msg","上传服务器失败！");
            return resultMap;
        }
    }
}
