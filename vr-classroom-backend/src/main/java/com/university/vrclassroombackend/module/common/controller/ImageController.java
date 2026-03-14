package com.university.vrclassroombackend.module.common.controller;

import com.university.vrclassroombackend.common.dto.ApiResponse;
import com.university.vrclassroombackend.constant.AppConstants;
import com.university.vrclassroombackend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/images")
@Tag(name = "图片管理", description = "图片上传相关接口")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.upload.server-path:/opt/1panel/apps/openresty/openresty/www/sites/vr-static/index}")
    private String serverUploadPath;

    @Value("${file.access.url:/assets}")
    private String accessUrl;

    @Value("${server.base-url:}")
    private String serverBaseUrl;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};

    @PostMapping("/post")
    @Operation(summary = "上传帖子图片", description = "上传帖子图片，返回图片URL")
    public ResponseEntity<?> uploadPostImage(
            @Parameter(description = "图片文件", required = true) @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute(AppConstants.Auth.USER_ID_ATTRIBUTE);
        if (userId == null) {
            logger.warn("上传图片失败: 未认证");
            return ResponseEntity.status(401).body(ApiResponse.error(401, "请先登录"));
        }

        // 校验文件
        if (file.isEmpty()) {
            logger.warn("上传图片失败: 文件为空");
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "请选择要上传的图片"));
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            logger.warn("上传图片失败: 文件过大, size={}", file.getSize());
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "图片大小不能超过5MB"));
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!isValidExtension(extension)) {
            logger.warn("上传图片失败: 不支持的文件类型, filename={}", originalFilename);
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "只支持 jpg、jpeg、png、gif、webp 格式的图片"));
        }

        try {
            // 确定存储路径
            String basePath = getUploadBasePath();

            // 确保基础目录存在
            File baseDir = new File(basePath);
            if (!baseDir.exists()) {
                baseDir.mkdirs();
                logger.info("创建上传目录: {}", basePath);
            }

            // 生成存储路径: posts/{yyyy}/{MM}/{dd}/
            LocalDate now = LocalDate.now();
            String datePath = String.format("posts/%s/%s/%s",
                    now.format(DateTimeFormatter.ofPattern("yyyy")),
                    now.format(DateTimeFormatter.ofPattern("MM")),
                    now.format(DateTimeFormatter.ofPattern("dd")));

            // 生成文件名: userId + 随机串.jpg
            String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            String newFilename = userId + randomString + "." + extension;

            // 完整路径
            Path targetPath = Paths.get(basePath, datePath, newFilename);

            // 创建目录（包括日期目录）
            Files.createDirectories(targetPath.getParent());

            // 保存文件
            file.transferTo(targetPath.toFile());

            // 生成访问URL
            String fileUrl = String.format("%s/%s/%s", accessUrl, datePath, newFilename);
            
            // 如果配置了服务器基础URL，添加完整的URL前缀
            if (serverBaseUrl != null && !serverBaseUrl.isEmpty()) {
                // 确保serverBaseUrl不以/结尾
                String baseUrl = serverBaseUrl.endsWith("/") ? serverBaseUrl.substring(0, serverBaseUrl.length() - 1) : serverBaseUrl;
                // 确保fileUrl不以/开头
                String urlPath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
                fileUrl = baseUrl + "/" + urlPath;
            }

            logger.info("上传图片成功: userId={}, filename={}, url={}", userId, newFilename, fileUrl);

            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);

            return ResponseEntity.ok().body(ApiResponse.success(data));

        } catch (IOException e) {
            logger.error("上传图片失败: IO异常", e);
            return ResponseEntity.status(500).body(ApiResponse.error(500, "图片上传失败，请稍后重试"));
        }
    }

    /**
     * 获取上传基础路径
     * 优先使用服务器路径，确保存储到指定的服务器目录
     */
    private String getUploadBasePath() {
        // 优先使用服务器路径，确保存储到指定的服务器目录
        String targetPath = serverUploadPath;
        File targetDir = new File(targetPath);
        
        // 确保目录存在
        if (!targetDir.exists()) {
            boolean created = targetDir.mkdirs();
            if (created) {
                logger.info("创建服务器上传目录: {}", targetPath);
            } else {
                logger.warn("创建服务器上传目录失败: {}", targetPath);
                // 如果创建失败，尝试使用本地路径
                targetPath = uploadPath;
                if (!new File(targetPath).isAbsolute()) {
                    String projectDir = System.getProperty("user.dir");
                    targetPath = new File(projectDir, targetPath).getAbsolutePath();
                    logger.info("使用本地上传目录: {}", targetPath);
                }
                // 确保本地目录存在
                File localDir = new File(targetPath);
                if (!localDir.exists()) {
                    localDir.mkdirs();
                    logger.info("创建本地上传目录: {}", targetPath);
                }
            }
        }
        
        return targetPath;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 校验文件扩展名是否有效
     */
    private boolean isValidExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
