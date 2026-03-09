package com.aicode.controller;

import com.aicode.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${storage.local.path:./uploads}")
    private String uploadPath;

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(400, "只允许上传图片文件");
        }

        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.')).toLowerCase()
                : "";
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            return Result.error(400, "只支持 jpg/png/gif/webp 格式");
        }

        try {
            String filename = UUID.randomUUID() + ext;
            Path dir = Paths.get(uploadPath, "avatars");
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename));
            return Result.success("/uploads/avatars/" + filename);
        } catch (IOException e) {
            log.error("Avatar upload failed", e);
            return Result.error(500, "文件上传失败");
        }
    }
}
