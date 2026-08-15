package com.learnai.service;

import com.learnai.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 上传文件存储：UUID 重命名 + 类型子目录 + 白名单校验 + 路径穿越防护
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

    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("yyyyMM");

    private final Path root;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /** 存储学习资源文件，返回相对路径（如 resources/202608/abc.pdf） */
    public String storeResourceFile(MultipartFile file) {
        return store(file, "resources", RESOURCE_EXTS, "资源");
    }

    /** 存储 3D 模型文件 */
    public String storeModelFile(MultipartFile file) {
        return store(file, "models", MODEL_EXTS, "模型");
    }

    /** 存储图片文件 */
    public String storeImage(MultipartFile file) {
        return store(file, "previews", IMAGE_EXTS, "图片");
    }

    private String store(MultipartFile file, String type, Set<String> allowed, String typeName) {
        if (file == null || file.isEmpty()) {
            throw ApiException.badRequest(typeName + "文件不能为空");
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = extensionOf(original);
        if (!allowed.contains(ext)) {
            throw ApiException.badRequest(
                    "不支持的文件类型 ." + ext + "（允许: " + String.join(" / ", allowed) + "）");
        }
        String name = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        String subDir = LocalDate.now().format(MONTH);
        Path dir = root.resolve(type).resolve(subDir).normalize();
        if (!dir.startsWith(root)) {
            throw ApiException.badRequest("非法上传路径");
        }
        try {
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(name));
        } catch (IOException e) {
            throw new IllegalStateException("文件保存失败: " + e.getMessage(), e);
        }
        return type + "/" + subDir + "/" + name;
    }

    /** 按存储的相对路径加载文件（防路径穿越） */
    public Resource load(String storedPath) {
        Path p = root.resolve(storedPath).normalize();
        if (!p.startsWith(root)) {
            throw ApiException.badRequest("非法文件路径");
        }
        try {
            return new UrlResource(p.toUri());
        } catch (MalformedURLException e) {
            throw new IllegalStateException("无法加载文件: " + storedPath, e);
        }
    }

    private String extensionOf(String filename) {
        int i = filename.lastIndexOf('.');
        return i >= 0 && i < filename.length() - 1
                ? filename.substring(i + 1).toLowerCase(Locale.ROOT) : "";
    }
}
