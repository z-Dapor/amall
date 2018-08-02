package com.zhp.service;

import com.zhp.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2018/7/25.
 */
public interface IFileService {
    ServerResponse upload(MultipartFile file, String path);

}
