/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: FileServiceImpl
 * Author:   臧浩鹏
 * Date:     2018/7/25 12:17
 * Description: FileService的实现类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.zhp.service.impl;

import com.google.common.collect.Lists;
import com.zhp.common.ServerResponse;
import com.zhp.service.IFileService;
import com.zhp.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 〈一句话功能简述〉<br> 
 * 〈FileService的实现类〉
 *
 * @author 臧浩鹏
 * @create 2018/7/25
 * @since 1.0.0
 */
@Service("iFileService")
@Slf4j
public class FileServiceImpl implements IFileService{
        @Override
        public ServerResponse upload(MultipartFile file, String path){
            String fileName = file.getOriginalFilename();
            String changeName = UUID.randomUUID().toString()+fileName;
            int size = (int) file.getSize();
            File dest = new File(path + "/" + changeName);
            //判断文件父目录是否存在
            if(!dest.getParentFile().exists()){
                dest.getParentFile().mkdir();
            }
            try {
                //保存文件
                file.transferTo(dest);
                boolean isOk = FTPUtil.uploadFile(Lists.newArrayList(dest));
                if(isOk){
                    dest.delete();
                    return ServerResponse.createBySuccess("上传FTP服务器成功!",dest.getName());
                }else {
                    log.error("上传FTP服务器失败！");
                }
                return ServerResponse.createByErrorMessage("上传FTP服务器失败!");
            } catch (IllegalStateException e) {
                return ServerResponse.createByErrorMessage("出现错误！");
            } catch (IOException e) {
                return ServerResponse.createByErrorMessage("保存文件失败！！");
            }
        }
}
