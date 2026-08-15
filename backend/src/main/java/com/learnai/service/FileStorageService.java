package com.learnai.service;

import com.learnai.service.storage.StorageStrategyFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * 上传文件存储门面：对外提供业务语义方法，实际存取委托给
 * {@link StorageStrategyFactory} 选择的存储策略（默认本地磁盘，可扩展对象存储）。
 */
@Service
public class FileStorageService {

    /** 学习资源附件白名单 */
    private static final Set<String> RESOURCE_EXTS =
            Set.of("pdf", "doc", "docx", "ppt", "pptx", "zip", "rar", "blend", "obj", "fbx", "stl", "dae", "3ds");

    /** 3D 模型文件白名单（比资源多 glb/gltf，供 Three.js 在线预览） */
    private static final Set<String> MODEL_EXTS =
            Set.of("obj", "fbx", "stl", "dae", "3ds", "glb", "gltf", "blend");

    /** 图片白名单 */
    private static final Set<String> IMAGE_EXTS = Set.of("jpg", "jpeg", "png", "gif");

    private final StorageStrategyFactory factory;

    public FileStorageService(StorageStrategyFactory factory) {
        this.factory = factory;
    }

    /** 存储学习资源文件，返回相对路径（如 resources/202608/abc.pdf） */
    public String storeResourceFile(MultipartFile file) {
        return factory.defaultStrategy().store(file, "resources", RESOURCE_EXTS, "资源");
    }

    /** 存储 3D 模型文件 */
    public String storeModelFile(MultipartFile file) {
        return factory.defaultStrategy().store(file, "models", MODEL_EXTS, "模型");
    }

    /** 存储图片文件 */
    public String storeImage(MultipartFile file) {
        return factory.defaultStrategy().store(file, "previews", IMAGE_EXTS, "图片");
    }

    /** 按存储的相对路径加载文件（防路径穿越） */
    public Resource load(String storedPath) {
        return factory.defaultStrategy().load(storedPath);
    }
}
