package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 配置类，用于创建AliOssUtil对象
 */
@Configuration//表明这是一个配置类
@Slf4j
public class OssConfiguration {

    @Bean   //将以下方法的返回值添加到IOC容器中，该方法的返回值类型为AliOssUtil
    @ConditionalOnMissingBean   // 判断当前IOC容器中是否有AliOssUtil对象，如果没有，则创建
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("开始创建阿里云文件上传工具类对象：{}", aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint(),
                aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(),
                aliOssProperties.getBucketName());
    }
}
