package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 通用接口
 */
// @RestController 标识这是一个控制器类，返回的数据直接写入响应体中，一般用于提供 RESTful API
@RestController
@RequestMapping("/admin/common")
@Api("通用接口")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;  // 注入AliOssUtil对象

    /**
     * 文件上传接口
     *
     * @param file 上传的文件
     * @return 文件路径
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传接口")
    // MultipartFile 用于接收上传的文件
    public Result<String> upload(MultipartFile file) { // 返回文件路径
        log.info("文件上传：{}", file);
        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            // 截取原始文件名的后缀 substring的参数为开始截的位置
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + suffix; // 使用UUID生成文件名，防止文件名重复
            // 获取文件的访问路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return  Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
