package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "通用接口")
public class CommonController {


    @Autowired
    private AliOssUtil aliOssUtil;


    /**
     * 文件上传
     * 图片不显示是前端问题，
     * 前端路径：https://sky-itcast.oss-cn-beijing.aliyuncs.com/7a55b845-1f2b-41fa-9486-76d187ee9ee1.png
     * 后端路径：https://sky-take-out-whl.oss-cn-beijing.aliyuncs.com/7a73365c-f24f-418a-9267-a3ab30bbaecb.jpg
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file)  {
        log.info("文件上传，文件名：{}", file);
//        String filePath = commonService.upload(file);
        //原始文件名
        String originalFilename = file.getOriginalFilename();

        //截取原始文件名的后缀  .png
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        //构造新文件名称
        String objectName = UUID.randomUUID().toString() + extension;

        //文件的请求路径
        try {
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            log.info("文件上传成功，文件路径：{}", filePath);
            return Result.success(filePath);  // ← 返回完整 OSS URL
        } catch (IOException e) {
            log.error("文件上传失败：{}", e);
//            return Result.error("文件上传失败");
//            throw new RuntimeException(e);
        }

        return null;

    }
}
