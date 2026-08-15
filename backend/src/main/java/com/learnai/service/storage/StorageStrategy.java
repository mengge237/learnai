package com.learnai.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * 文件存储策略（策略模式）：本地磁盘 / 对象存储（OSS 等）可插拔。
 * 每种策略实现一套「存 + 取」，工厂按名称选择。
 */
public interface StorageStrategy {

    /** 策略名（工厂注册键），如 "local" */
    String name();

    /**
     * 存储文件。
     *
     * @param file     上传文件
     * @param type     业务类型目录（resources / models / previews）
     * @param allowed  允许的扩展名白名单
     * @param typeName 业务类型中文名（用于报错文案）
     * @return 相对存储路径（如 models/202608/abc.obj）
     */
    String store(MultipartFile file, String type, Set<String> allowed, String typeName);

    /** 按存储的相对路径加载文件（须防路径穿越） */
    Resource load(String storedPath);
}
