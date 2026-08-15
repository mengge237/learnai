package com.learnai.service.storage;

import com.learnai.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
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
 * 本地磁盘存储策略：UUID 重命名 + 类型子目录 + 白名单校验 + 路径穿越防护。
 */
@Component
public class LocalStorageStrategy implements StorageStrategy {

    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("yyyyMM");

    private final Path root;

    public LocalStorageStrategy(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Override
    public String name() {
        return "local";
    }

    @Override
    public String store(MultipartFile file, String type, Set<String> allowed, String typeName) {
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

    @Override
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
