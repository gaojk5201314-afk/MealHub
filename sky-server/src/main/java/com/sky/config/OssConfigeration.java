package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/*创建AliOssUtil的实体化对象*/
@Configuration
@Slf4j
public class OssConfigeration {
    /*ConditionalOnMissingBean注解是当容器中缺少该Bean时，才创建当前Bean，防止重复创建Bean导致冲突报异常*/
    @ConditionalOnMissingBean
    /*Bean注解是代表程序启动时自动执行这个方法*/
    @Bean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssUtil) {
        /*AliOssUtil是创建阿里云默认参数的方法，也就是用来配置阿里云的链接参数，为上传文件做准备*/
        log.info("创建AliOssUtil的实体化对象{}",aliOssUtil);
        return new AliOssUtil(aliOssUtil.getEndpoint(),
                aliOssUtil.getAccessKeyId(),
                aliOssUtil.getAccessKeySecret(),
                aliOssUtil.getBucketName());
    }
}
