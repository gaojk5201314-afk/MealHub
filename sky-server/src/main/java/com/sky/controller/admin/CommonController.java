package com.sky.controller.admin;


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
import java.util.UUID;
/*上传文件通用接口，全部上传文件(图片等)的功能都是调用这个接口*/
@RestController
@RequestMapping("/admin/common")
@Api("通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aou;

    @PostMapping("/upload")
    @ApiOperation("上传文件")
    public Result<String> upload(MultipartFile file){
        log.info("上传文件{}",file);

        try {
            String originalFilename = file.getOriginalFilename();
            String s = originalFilename.split("\\.")[1];
            String s1 = UUID.randomUUID().toString() + "." +s;
            String upload = aou.upload(file.getBytes(), s1);
            return Result.success(upload);
        } catch (IOException e) {
            log.info("上传文件失败{}",e);
        }

        return Result.error("上传文件失败");
    }

}
