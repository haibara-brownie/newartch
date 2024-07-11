package com.cloudwise.archetype.middleware.minio;

import com.cloudwise.storage.FileInfo;
import com.cloudwise.storage.FileStorageService;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * @author wangpei
 * @date 2023-10-17
 * 文件操作示例
 */
public class OssOperate {

    /**
     * 注入的前提为启动类添加了@EnableFileStorage注解
     */
    @Resource
    private FileStorageService fileStorageService;

    /**
     * 桶操作
     */
    private void bucketOperation() {
        // 判断bucket是否存在
        boolean bucketExists = fileStorageService.bucket("testbert1").exists();
        if (bucketExists) {
            // 移除bucket
            boolean removeBucket = fileStorageService.bucket("testbert1").remove();
        } else {
            // 创建bucket
            boolean createBucketWithoutConfig = fileStorageService.bucket("testbert1").create();
        }
    }

    /**
     * 文件操作
     */
    private void fileOperation() {

        // 下载文件
        fileStorageService.of("Users/bertling/Downloads/CMake-3.26.4/.DS_Store").setBucket("testkafka").download().file(new File("/Users/bertling/Downloads/QtSDK/test.txt"));

        // 上传文件夹
        List<FileInfo> uploadDir = fileStorageService.of(new File("/Users/bertling/Downloads/tree")).setBucket("testbert1").setFilepath("/55").upload();

        String fileName = "/Users/bertling/Downloads/tree/middle_data-1685088481370.json";
        // 上传文件
        List<FileInfo> uploadFile1 = fileStorageService.of(new File(fileName)).setBucket("testbert1").setFilepath("/test11135").upload();

        // 删除文件夹
        fileStorageService.of("/MultipartFiles111453").setBucket("testbert1").delete();
        // 删除文件
        fileStorageService.of("/MultipartFiles/test.txt").setBucket("testbert1").delete();
    }
}
