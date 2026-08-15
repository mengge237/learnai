package com.learnai.config;

import com.learnai.entity.*;
import com.learnai.entity.enums.*;
import com.learnai.repository.*;
import com.learnai.service.factory.StepTemplateFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 演示数据初始化：userRepository.count()==0 时播种中文演示数据。
 * 三种角色账号：admin/admin123（管理员）、auditor/audit123（审核员）、demo/demo123（普通用户）。
 * 同时生成占位文件（最小 PDF、ASCII 立方体 OBJ、渐变 PNG 预览图），保证下载/预览端到端可用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final ResourceCategoryRepository resourceCategoryRepository;
    private final ModelCategoryRepository modelCategoryRepository;
    private final LearningResourceRepository learningResourceRepository;
    private final LearningPathRepository learningPathRepository;
    private final PathResourceRepository pathResourceRepository;
    private final UserLearningPathRepository userLearningPathRepository;
    private final LearningRecordRepository learningRecordRepository;
    private final LearningStepRepository learningStepRepository;
    private final Model3DRepository model3DRepository;
    private final ModelOrderRepository modelOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CommentRepository commentRepository;
    private final FavoriteRepository favoriteRepository;
    private final DownloadRepository downloadRepository;
    private final AiInteractionRepository aiInteractionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    private static final DateTimeFormatter MONTH = DateTimeFormatter.ofPattern("yyyyMM");
    /** 默认步骤标题（与 LearningProgressService 共用的正文模板见该服务） */
    private static final List<String> STEP_TITLES = StepTemplateFactory.all().stream()
            .map(StepTemplateFactory.StepTemplate::title).toList();

    private Path root;
    private Long adminId;
    private Long auditorId;
    private Long demoId;
    private Map<String, ResourceCategory> resCats;
    private Map<String, ModelCategory> modelCats;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("[DataInitializer] 已有用户数据，跳过种子数据初始化");
            return;
        }
        log.info("[DataInitializer] 开始初始化演示数据……");
        root = Paths.get(uploadDir).toAbsolutePath().normalize();

        seedRoles();
        seedUsers();
        seedCategories();
        seedResources();
        seedPaths();
        seedRecords();
        seedModels();
        seedComments();
        seedFavorites();
        seedDownloads();
        seedAiHistory();
        seedOrders();
        log.info("[DataInitializer] 演示数据初始化完成：admin/admin123、auditor/audit123、demo/demo123");
    }

    // ---------- 角色 ----------

    private void seedRoles() {
        if (userRoleRepository.count() > 0) {
            return;
        }
        UserRole admin = new UserRole();
        admin.setRoleName("管理员");
        admin.setAuthority("ROLE_ADMIN");
        UserRole auditor = new UserRole();
        auditor.setRoleName("审核员");
        auditor.setAuthority("ROLE_AUDITOR");
        UserRole user = new UserRole();
        user.setRoleName("普通用户");
        user.setAuthority("ROLE_USER");
        userRoleRepository.saveAll(List.of(admin, auditor, user));
    }

    // ---------- 用户 ----------

    private void seedUsers() {
        UserRole adminRole = userRoleRepository.findAll().stream()
                .filter(r -> "ROLE_ADMIN".equals(r.getAuthority())).findFirst().orElseThrow();
        UserRole auditorRole = userRoleRepository.findAll().stream()
                .filter(r -> "ROLE_AUDITOR".equals(r.getAuthority())).findFirst().orElseThrow();
        UserRole userRole = userRoleRepository.findAll().stream()
                .filter(r -> "ROLE_USER".equals(r.getAuthority())).findFirst().orElseThrow();

        User admin = user("admin", "admin123", adminRole, "平台管理员", "男", "北京市", "13800001001", "admin@learnai.com");
        User auditor = user("auditor", "audit123", auditorRole, "内容审核专员", "女", "上海市", "13800001002", "auditor@learnai.com");
        User demo = user("demo", "demo123", userRole, "热爱 3D 建模的学习者", "男", "广东省广州市",
                "13800001003", "demo@learnai.com");
        admin.setStudentNo("T0001");
        auditor.setStudentNo("T0002");
        demo.setStudentNo("2026010016");
        // 工业风默认偏好：工业橙强调色 + 浅灰边框
        for (User seed : List.of(admin, auditor, demo)) {
            seed.setThemeColor("#e8590c");
            seed.setBorderColor("#d0d0d0");
        }
        demo.setProvince("广东省");
        demo.setCity("广州市");
        demo.setDefaultShippingAddress("广东省广州市天河区演示路 1 号");
        demo.setBirthdate(java.time.LocalDate.of(2002, 5, 20));

        userRepository.saveAll(List.of(admin, auditor, demo));
        adminId = admin.getUserId();
        auditorId = auditor.getUserId();
        demoId = demo.getUserId();
    }

    private User user(String username, String rawPassword, UserRole role, String bio,
                      String gender, String location, String phone, String email) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setBio(bio);
        u.setGender(gender);
        u.setLocation(location);
        u.setPhone(phone);
        u.setEmail(email);
        u.setIsActive(true);
        u.setCreatedAt(LocalDateTime.now().minusDays(30));
        return u;
    }

    // ---------- 分类 ----------

    private void seedCategories() {
        // 学习资源分类（5 个根分类 + 子分类）
        ResourceCategory c3d = cat("3D建模", "三维建模基础与高级技巧", null, 1);
        ResourceCategory cGame = cat("游戏开发", "游戏引擎与交互开发", null, 2);
        ResourceCategory cGraphics = cat("图形学", "计算机图形学与渲染", null, 3);
        ResourceCategory cFrontend = cat("前端开发", "Web 前端与 3D 网页开发", null, 4);
        ResourceCategory cDesign = cat("平面设计", "贴图绘制与视觉设计", null, 5);
        resourceCategoryRepository.saveAll(List.of(c3d, cGame, cGraphics, cFrontend, cDesign));

        resourceCategoryRepository.saveAll(List.of(
                cat("Blender 入门", "Blender 建模基础教程", c3d, 1),
                cat("Maya 实战", "Maya 案例实战教程", c3d, 2),
                cat("3ds Max", "3ds Max 效果图与渲染", c3d, 3),
                cat("ZBrush 雕刻", "数字雕刻入门与进阶", c3d, 4),
                cat("Unity 基础", "Unity 引擎 3D 游戏开发", cGame, 1),
                cat("UE5 入门", "虚幻引擎 5 场景与蓝图", cGame, 2),
                cat("WebGL", "原生 WebGL 图形编程", cGraphics, 1),
                cat("着色器", "GLSL/HLSL 着色器语言", cGraphics, 2),
                cat("Three.js", "Three.js 3D 网页开发", cFrontend, 1),
                cat("纹理绘制", "次世代纹理与 Substance", cDesign, 1)
        ));
        resCats = resourceCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(ResourceCategory::getCategoryName, Function.identity()));

        // 模型商城分类
        modelCats = List.of(
                mcat("角色模型", "人物与动物角色 3D 模型"),
                mcat("建筑模型", "建筑与室内场景模型"),
                mcat("机械模型", "机械与载具部件模型"),
                mcat("场景模型", "游戏与影视场景资源包"),
                mcat("交通工具", "车辆飞行器等交通工具模型"),
                mcat("动物模型", "写实与卡通动物模型")
        ).stream().collect(Collectors.toMap(ModelCategory::getCategoryName, Function.identity()));
        modelCategoryRepository.saveAll(modelCats.values());
    }

    private ResourceCategory cat(String name, String desc, ResourceCategory parent, int sort) {
        ResourceCategory c = new ResourceCategory();
        c.setCategoryName(name);
        c.setDescription(desc);
        c.setParentCategoryId(parent == null ? null : parent.getCategoryId());
        c.setSortOrder(sort);
        c.setIsActive(true);
        return c;
    }

    private ModelCategory mcat(String name, String desc) {
        ModelCategory c = new ModelCategory();
        c.setCategoryName(name);
        c.setDescription(desc);
        return c;
    }

    // ---------- 学习资源（16 个，其中 3 条待审核） ----------

    private void seedResources() {
        List<LearningResource> resources = List.of(
                res("Blender 基础建模入门", "李明", "RES-001", null, "Blender 入门", "入门", 90, "视频",
                        "从零开始认识 Blender 界面，掌握基础网格编辑、挤出、环切等核心建模操作，完成第一个 3D 作品。",
                        true, 0, 3560, 412, 689),
                res("Blender 材质与灯光", "李明", "RES-002", null, "Blender 入门", "初级", 120, "视频",
                        "深入讲解 PBR 材质原理、节点编辑器使用与三点布光法，让你的模型质感提升一个档次。",
                        true, 1, 2280, 267, 341),
                res("Blender 动画制作实战", "王芳", "RES-003", new BigDecimal("29.90"), "Blender 入门", "中级", 180, "视频",
                        "关键帧动画、骨骼绑定与权重绘制全流程实战，制作一段完整的角色动画短片。",
                        false, 2, 1560, 189, 220, 1L),
                res("Maya 角色建模全流程", "张伟", "RES-004", new BigDecimal("49.90"), "Maya 实战", "高级", 240, "视频",
                        "从概念图到高模全流程：拓扑结构规划、布线技巧、细节雕刻与 UV 展开。",
                        false, 3, 980, 156, 143),
                res("Maya 硬表面建模技巧", "张伟", "RES-005", null, "Maya 实战", "中级", 60, "图文",
                        "机械硬表面建模方法论：倒角策略、布尔运算与细分曲面的平衡。",
                        true, 4, 1340, 98, 176),
                res("3ds Max 室内效果图渲染", "刘洋", "RES-006", new BigDecimal("39.90"), "3ds Max", "中级", 200, "视频",
                        "V-Ray 渲染器实战：材质调整、灯光布置与后期处理，产出商业级室内效果图。",
                        false, 5, 1120, 121, 156),
                res("Unity 3D 游戏开发入门", "陈静", "RES-007", null, "Unity 基础", "入门", 150, "视频",
                        "使用 Unity 引擎搭建第一个 3D 游戏：场景搭建、角色控制、碰撞检测与 UI 系统。",
                        true, 6, 4210, 389, 512),
                res("Unity 物理引擎实战", "陈静", "RES-008", null, "Unity 基础", "初级", 90, "练习",
                        "Rigidbody 刚体、碰撞体与关节组件的进阶应用，附 12 个可运行练习场景。",
                        true, 7, 1870, 145, 203),
                res("Unreal Engine 5 场景搭建", "赵磊", "RES-009", new BigDecimal("59.90"), "UE5 入门", "中级", 220, "视频",
                        "Nanite 与 Lumen 两大核心特性详解，搭建电影级光影场景。",
                        false, 8, 1450, 201, 167),
                res("WebGL 图形编程基础", "孙倩", "RES-010", null, "WebGL", "初级", 100, "图文",
                        "从渲染管线讲起：顶点着色器、片元着色器与缓冲区对象，手写第一个 WebGL 程序。",
                        true, 9, 2010, 176, 289),
                res("GLSL 着色器语言入门", "孙倩", "RES-011", new BigDecimal("19.90"), "着色器", "中级", 80, "源码",
                        "GLSL 语法精讲与 15 个实用着色器案例源码（渐变、噪声、水波、卡通描边等）。",
                        false, 10, 890, 132, 108),
                res("Three.js 3D 网页开发", "周杰", "RES-012", null, "Three.js", "入门", 120, "源码",
                        "零基础掌握 Three.js：场景、相机、渲染器、材质与光照，附完整项目源码。",
                        true, 11, 5230, 478, 634, 10L),
                res("Three.js 高级渲染技巧", "周杰", "RES-013", new BigDecimal("24.90"), "Three.js", "高级", 140, "源码",
                        "后处理特效、性能优化、阴影调参与 GLTF 工作流，进阶 Three.js 渲染表现。",
                        false, 12, 1670, 210, 187),
                res("次世代纹理绘制实战", "吴敏", "RES-014", new BigDecimal("34.90"), "纹理绘制", "中级", 160, "视频",
                        "Substance Painter 完整工作流：烘焙法线、智能材质与导出到引擎。",
                        false, 13, 760, 95, 102),
                res("ZBrush 数字雕刻入门", "郑浩", "RES-015", null, "ZBrush 雕刻", "入门", 130, "视频",
                        "认识 ZBrush 核心笔刷与 DynaMesh，雕刻第一个数字头像。",
                        true, 14, 1340, 168, 251),
                res("低多边形风格化建模", "李明", "RES-016", null, "Blender 入门", "初级", 70, "练习",
                        "低多边形（Low Poly）风格化建模：色彩搭配、光影氛围与导出全流程练习。",
                        true, 15, 980, 87, 129)
        );

        // 最后 3 条设为待审核（demo 用户提交）
        for (int i = resources.size() - 3; i < resources.size(); i++) {
            LearningResource r = resources.get(i);
            r.setIsApproved(false);
            r.setApprovedBy(null);
            r.setApprovedDate(null);
            r.setAuthor("demo");
        }
        learningResourceRepository.saveAll(resources);

        // 生成占位文件与预览图
        for (LearningResource r : resources) {
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            r.setFilePath(writeFile("resources", uuid + ".pdf", MINIMAL_PDF));
            r.setOriginalFileName(r.getResourceTitle() + "（演示资料）.pdf");
            r.setPreviewUrl("/uploads/" + writeFile("previews", uuid + ".png",
                    gradientPng(r.getResourceId().intValue())));
        }
        learningResourceRepository.saveAll(resources);
    }

    private LearningResource res(String title, String author, String code, BigDecimal price, String catName,
                                 String difficulty, int minutes, String type, String desc,
                                 boolean isFree, int daysAgo, int views, int likes, int completions, Long prerequisite) {
        LearningResource r = new LearningResource();
        r.setResourceTitle(title);
        r.setAuthor(author);
        r.setResourceCode(code);
        r.setPrice(price == null ? BigDecimal.ZERO : price);
        r.setCategory(resCats.get(catName));
        r.setIsPublic(true);
        r.setCreateDate(LocalDateTime.now().minusDays(daysAgo).minusHours(3));
        r.setIsApproved(true);
        r.setApprovedBy(auditorId);
        r.setApprovedDate(r.getCreateDate().plusHours(5));
        r.setDescription(desc);
        r.setDifficultyLevel(difficulty);
        r.setDurationMinutes(minutes);
        r.setLearningType(type);
        r.setViewCount(views);
        r.setLikeCount(likes);
        r.setCompletionCount(completions);
        r.setIsFree(isFree);
        r.setPrerequisiteResourceId(prerequisite);
        return r;
    }

    private LearningResource res(String title, String author, String code, BigDecimal price, String catName,
                                 String difficulty, int minutes, String type, String desc,
                                 boolean isFree, int daysAgo, int views, int likes, int completions) {
        return res(title, author, code, price, catName, difficulty, minutes, type, desc,
                isFree, daysAgo, views, likes, completions, null);
    }

    // ---------- 学习路径（4 条） ----------

    private void seedPaths() {
        List<LearningResource> resources = learningResourceRepository.findAll();
        Map<String, LearningResource> byCode = resources.stream()
                .collect(Collectors.toMap(LearningResource::getResourceCode, Function.identity()));

        LearningPath p1 = path("Blender 零基础到进阶之路",
                "从界面认识到动画制作，系统掌握 Blender 全流程技能，适合完全零基础的学习者。",
                "零基础学习者、3D 爱好者", 40, 1, 1250, 320, 20);
        LearningPath p2 = path("3D 游戏开发工程师路线",
                "Unity 入门到物理引擎再到 UE5 场景搭建，覆盖主流游戏引擎开发技能。",
                "有一定编程基础、想进入游戏行业的开发者", 60, 2, 980, 210, 18);
        LearningPath p3 = path("Web 3D 图形开发路线",
                "从 WebGL 原理到 Three.js 实战，掌握在浏览器中构建 3D 应用的全套技能。",
                "前端开发者、网页 3D 应用爱好者", 45, 2, 760, 180, 15);
        LearningPath p4 = path("3D 角色设计大师之路",
                "Maya 建模、ZBrush 雕刻与次世代纹理，打造完整的角色设计能力树。",
                "有一定建模基础、想成为角色设计师的学习者", 80, 3, 540, 95, 12);
        learningPathRepository.saveAll(List.of(p1, p2, p3, p4));

        // 路径资源序列
        pathResourceRepository.saveAll(List.of(
                pr(p1, byCode.get("RES-001"), 1), pr(p1, byCode.get("RES-002"), 2),
                pr(p1, byCode.get("RES-016"), 3), pr(p1, byCode.get("RES-003"), 4),
                pr(p2, byCode.get("RES-007"), 1), pr(p2, byCode.get("RES-008"), 2),
                pr(p2, byCode.get("RES-009"), 3),
                pr(p3, byCode.get("RES-010"), 1), pr(p3, byCode.get("RES-011"), 2),
                pr(p3, byCode.get("RES-012"), 3), pr(p3, byCode.get("RES-013"), 4),
                pr(p4, byCode.get("RES-004"), 1), pr(p4, byCode.get("RES-005"), 2),
                pr(p4, byCode.get("RES-015"), 3), pr(p4, byCode.get("RES-014"), 4)
        ));

        // demo 报名两条路径
        userLearningPathRepository.saveAll(List.of(
                ulp(p1, PathStatus.InProgress, 50.0, 10),
                ulp(p2, PathStatus.InProgress, 33.0, 6)
        ));
    }

    private LearningPath path(String name, String desc, String audience, int hours, int difficulty,
                              int views, int enrollments, int daysAgo) {
        LearningPath p = new LearningPath();
        p.setPathName(name);
        p.setDescription(desc);
        p.setTargetAudience(audience);
        p.setEstimatedHours(hours);
        p.setDifficultyLevel(difficulty);
        p.setViewCount(views);
        p.setEnrollmentCount(enrollments);
        p.setCreateDate(LocalDateTime.now().minusDays(daysAgo));
        p.setIsActive(true);
        return p;
    }

    private PathResource pr(LearningPath path, LearningResource resource, int seq) {
        PathResource p = new PathResource();
        p.setPathId(path.getPathId());
        p.setResourceId(resource.getResourceId());
        p.setSequenceNumber(seq);
        return p;
    }

    private UserLearningPath ulp(LearningPath path, PathStatus status, double progress, int daysAgo) {
        UserLearningPath u = new UserLearningPath();
        u.setUserId(demoId);
        u.setPathId(path.getPathId());
        u.setEnrollDate(LocalDateTime.now().minusDays(daysAgo));
        u.setStatus(status);
        u.setProgress(progress);
        return u;
    }

    // ---------- demo 学习记录（近 7 天分布，供分析图表使用） ----------

    private void seedRecords() {
        List<LearningResource> resources = learningResourceRepository.findAll();
        Map<String, LearningResource> byCode = resources.stream()
                .collect(Collectors.toMap(LearningResource::getResourceCode, Function.identity()));

        // 已完成：Blender 基础建模入门（6 天前完成）
        LearningRecord done = record(byCode.get("RES-001"), 7, LearningStatus.Completed, 100.0, 92,
                "系统学完了基础建模部分，收获很大！", 150);
        learningRecordRepository.save(done);
        for (int i = 1; i <= 3; i++) {
            LearningStep s = step(done, byCode.get("RES-001"), i, StepStatus.Completed);
            s.setCompletedTime(done.getStartTime().plusMinutes(50L * i));
            learningStepRepository.save(s);
        }

        // 进行中：Blender 材质与灯光（3 天前开始，第 2 步进行中）
        LearningRecord r2 = record(byCode.get("RES-002"), 3, LearningStatus.InProgress, 60.0, null,
                "正在学习材质节点部分", 75);
        learningRecordRepository.save(r2);
        learningStepRepository.save(step(r2, byCode.get("RES-002"), 1, StepStatus.Completed));
        learningStepRepository.save(step(r2, byCode.get("RES-002"), 2, StepStatus.InProgress));
        learningStepRepository.save(step(r2, byCode.get("RES-002"), 3, StepStatus.NotStarted));

        // 进行中：Unity 3D 游戏开发入门（1 天前开始）
        LearningRecord r3 = record(byCode.get("RES-007"), 1, LearningStatus.InProgress, 35.0, null,
                "刚完成角色控制部分", 40);
        learningRecordRepository.save(r3);
        learningStepRepository.save(step(r3, byCode.get("RES-007"), 1, StepStatus.Completed));
        learningStepRepository.save(step(r3, byCode.get("RES-007"), 2, StepStatus.InProgress));
        learningStepRepository.save(step(r3, byCode.get("RES-007"), 3, StepStatus.NotStarted));
    }

    private LearningRecord record(LearningResource resource, int daysAgo, LearningStatus status,
                                  double progress, Integer score, String notes, int minutes) {
        LearningRecord r = new LearningRecord();
        r.setUserId(demoId);
        r.setResourceId(resource.getResourceId());
        r.setStartTime(LocalDateTime.now().minusDays(daysAgo).minusHours(4));
        if (status == LearningStatus.Completed) {
            r.setEndTime(r.getStartTime().plusHours(3));
        }
        r.setStatus(status);
        r.setProgress(progress);
        r.setScore(score);
        r.setNotes(notes);
        r.setDurationMinutes(minutes);
        return r;
    }

    private LearningStep step(LearningRecord record, LearningResource resource, int number, StepStatus status) {
        LearningStep s = new LearningStep();
        s.setRecordId(record.getRecordId());
        s.setStepNumber(number);
        s.setStepTitle(STEP_TITLES.get(number - 1));
        s.setStepContent(StepTemplateFactory.templateOf(number).render(resource.getResourceTitle()));
        s.setStatus(status);
        if (status == StepStatus.Completed) {
            s.setCompletedTime(record.getStartTime().plusMinutes(45L * number));
        }
        s.setDurationSeconds(45 * 60);
        return s;
    }

    // ---------- 3D 模型（8 个，其中 2 条待审核） ----------

    private void seedModels() {
        List<Model3D> models = List.of(
                model("卡通风格少女角色", "晨曦工作室", "MOD-001", null, "角色模型", 20, 3560),
                model("低多边形战士角色", "晨曦工作室", "MOD-002", new BigDecimal("19.90"), "角色模型", 18, 2180),
                model("现代简约别墅", "筑梦空间", "MOD-003", new BigDecimal("49.90"), "建筑模型", 16, 1980),
                model("未来科幻战斗机", "星环工坊", "MOD-004", new BigDecimal("99.50"), "交通工具", 15, 1760),
                model("蒸汽朋克机器人", "齿轮公社", "MOD-005", new BigDecimal("29.90"), "机械模型", 14, 1540),
                model("卡通小狐狸", "萌物制造", "MOD-006", new BigDecimal("9.90"), "动物模型", 13, 4230),
                model("森林场景资源包", "绿野场景", "MOD-007", new BigDecimal("59.90"), "场景模型", 12, 1320),
                model("中式园林建筑群", "筑梦空间", "MOD-008", new BigDecimal("89.90"), "建筑模型", 11, 890)
        );

        // 最后 2 条设为待审核
        for (int i = models.size() - 2; i < models.size(); i++) {
            Model3D m = models.get(i);
            m.setIsApproved(false);
            m.setApprovedBy(null);
            m.setApprovedDate(null);
            m.setCreator("demo");
        }
        model3DRepository.saveAll(models);

        for (Model3D m : models) {
            String uuid = UUID.randomUUID().toString().substring(0, 8);
            m.setFilePath(writeFile("models", uuid + ".obj", CUBE_OBJ));
            m.setOriginalFileName(m.getModelName() + "（演示模型）.obj");
            m.setPreviewUrl("/uploads/" + writeFile("previews", uuid + ".png",
                    gradientPng(m.getModelId().intValue() + 16)));
        }
        model3DRepository.saveAll(models);
    }

    private Model3D model(String name, String creator, String code, BigDecimal price, String catName,
                          int daysAgo, int views) {
        Model3D m = new Model3D();
        m.setModelName(name);
        m.setCreator(creator);
        m.setModelCode(code);
        m.setPrice(price == null ? BigDecimal.ZERO : price);
        m.setCategory(modelCats.get(catName));
        m.setIsPublic(true);
        m.setCreateDate(LocalDateTime.now().minusDays(daysAgo).minusHours(5));
        m.setIsApproved(true);
        m.setApprovedBy(auditorId);
        m.setApprovedDate(m.getCreateDate().plusHours(6));
        return m;
    }

    // ---------- 评论（14 条，含回复） ----------

    private void seedComments() {
        List<LearningResource> resources = learningResourceRepository.findAll();
        Map<String, Long> resId = resources.stream()
                .collect(Collectors.toMap(LearningResource::getResourceCode, LearningResource::getResourceId));
        Map<String, Long> modId = model3DRepository.findAll().stream()
                .collect(Collectors.toMap(Model3D::getModelCode, Model3D::getModelId));

        Comment c1 = comment(demoId, resId.get("RES-001"), null, null, "讲得特别清楚，零基础也能跟上！", 6);
        commentRepository.save(c1);
        Comment c2 = comment(adminId, resId.get("RES-001"), null, c1.getCommentId(), "感谢支持，后续会持续更新进阶内容", 5);
        commentRepository.saveAll(List.of(c2,
                comment(auditorId, resId.get("RES-001"), null, null, "配套练习素材很实用，推荐！", 5),
                comment(demoId, resId.get("RES-002"), null, null, "灯光部分讲得不错，期待材质进阶篇", 4),
                comment(adminId, resId.get("RES-002"), null, null, "材质进阶篇已列入更新计划", 4),
                comment(demoId, resId.get("RES-012"), null, null, "Three.js 入门首选，代码注释很详细", 3),
                comment(demoId, resId.get("RES-007"), null, null, "跟着做完了第一个小游戏，成就感满满！", 3),
                comment(demoId, resId.get("RES-010"), null, null, "WebGL 基础概念讲得很透彻", 2),
                comment(auditorId, resId.get("RES-004"), null, null, "内容偏高级，建议先学完基础课程", 1),
                comment(demoId, null, modId.get("MOD-004"), null, "模型面数优化得很好，引擎里跑得很流畅", 3),
                comment(adminId, null, modId.get("MOD-004"), null, "感谢购买支持，使用中有问题随时反馈", 2),
                comment(demoId, null, modId.get("MOD-006"), null, "小狐狸太可爱了，直接下单", 2),
                comment(adminId, null, modId.get("MOD-001"), null, "推荐给角色建模初学者，布线很规范", 1),
                comment(demoId, null, modId.get("MOD-003"), null, "别墅模型细节丰富，渲染效果很棒", 1)
        ));
    }

    private Comment comment(Long userId, Long resourceId, Long modelId, Long parentId, String content, int daysAgo) {
        Comment c = new Comment();
        c.setUserId(userId);
        c.setResourceId(resourceId);
        c.setModelId(modelId);
        c.setParentCommentId(parentId);
        c.setContent(content);
        c.setCommentDate(LocalDateTime.now().minusDays(daysAgo).minusHours(2));
        c.setIsApproved(true);
        return c;
    }

    // ---------- 收藏与下载 ----------

    private void seedFavorites() {
        List<LearningResource> resources = learningResourceRepository.findAll();
        Map<String, Long> resId = resources.stream()
                .collect(Collectors.toMap(LearningResource::getResourceCode, LearningResource::getResourceId));
        Map<String, Long> modId = model3DRepository.findAll().stream()
                .collect(Collectors.toMap(Model3D::getModelCode, Model3D::getModelId));

        favoriteRepository.saveAll(List.of(
                fav(resId.get("RES-001"), null, 6),
                fav(resId.get("RES-002"), null, 4),
                fav(resId.get("RES-012"), null, 3),
                fav(null, modId.get("MOD-001"), 2),
                fav(null, modId.get("MOD-003"), 1)
        ));
    }

    private Favorite fav(Long resourceId, Long modelId, int daysAgo) {
        Favorite f = new Favorite();
        f.setUserId(demoId);
        f.setResourceId(resourceId);
        f.setModelId(modelId);
        f.setAddedDate(LocalDateTime.now().minusDays(daysAgo));
        return f;
    }

    private void seedDownloads() {
        List<LearningResource> resources = learningResourceRepository.findAll();
        Map<String, Long> resId = resources.stream()
                .collect(Collectors.toMap(LearningResource::getResourceCode, LearningResource::getResourceId));
        Map<String, Long> modId = model3DRepository.findAll().stream()
                .collect(Collectors.toMap(Model3D::getModelCode, Model3D::getModelId));

        downloadRepository.saveAll(List.of(
                dl(resId.get("RES-001"), null, 6),
                dl(resId.get("RES-012"), null, 3),
                dl(null, modId.get("MOD-001"), 2),
                dl(null, modId.get("MOD-006"), 1)
        ));
    }

    private Download dl(Long resourceId, Long modelId, int daysAgo) {
        Download d = new Download();
        d.setUserId(demoId);
        d.setResourceId(resourceId);
        d.setModelId(modelId);
        d.setDownloadTime(LocalDateTime.now().minusDays(daysAgo).minusHours(1));
        d.setIpAddress("127.0.0.1");
        return d;
    }

    // ---------- AI 对话历史（7 条） ----------

    private void seedAiHistory() {
        aiInteractionRepository.saveAll(List.of(
                ai("你好", "你好！我是你的学习助手，可以为你推荐学习资源、制定学习路径、分析学习进度，有什么可以帮你的吗？", "问候", 6),
                ai("推荐一些适合我的学习资源", "根据你的学习记录，为你推荐以下同类热门资源：《Blender 材质与灯光》《Blender 动画制作实战》《低多边形风格化建模》，都是 Blender 入门方向的优质内容！", "推荐", 5),
                ai("学习路径怎么安排？", "平台为你准备了多条学习路径：《Blender 零基础到进阶之路》《Web 3D 图形开发路线》《3D 游戏开发工程师路线》，建议按顺序完成路径中的资源，循序渐进。", "学习路径", 4),
                ai("我的学习进度怎么样？", "你已完成《Blender 基础建模入门》，当前正在学习《Blender 材质与灯光》（进度 60%）和《Unity 3D 游戏开发入门》（进度 35%），继续保持！", "学习进度", 3),
                ai("什么是法线贴图？", "法线贴图（Normal Map）是一种记录表面法线方向的纹理贴图，通过扰动光照让低模表现出高模的细节，是次世代游戏开发的核心技术之一。", "问题", 2),
                ai("谢谢你的帮助", "不客气！学习路上有任何问题都可以随时问我，祝你学习愉快！", "感谢", 1),
                ai("再见", "再见！期待下次和你一起学习，加油！", "告别", 0)
        ));
    }

    private AiInteraction ai(String userMsg, String reply, String topic, int daysAgo) {
        AiInteraction a = new AiInteraction();
        a.setUserId(demoId);
        a.setUserMessage(userMsg);
        a.setAiMessage(reply);
        a.setInteractionType("chat");
        a.setTopic(topic);
        a.setInteractionTime(LocalDateTime.now().minusDays(daysAgo).minusHours(3));
        return a;
    }

    // ---------- 订单（4 笔，覆盖各状态） ----------

    private void seedOrders() {
        Map<String, Long> modId = model3DRepository.findAll().stream()
                .collect(Collectors.toMap(Model3D::getModelCode, Model3D::getModelId));
        Map<String, BigDecimal> modPrice = model3DRepository.findAll().stream()
                .collect(Collectors.toMap(Model3D::getModelCode, m -> m.getPrice() == null ? BigDecimal.ZERO : m.getPrice()));

        order(10, OrderStatus.Completed,
                item(modId.get("MOD-004"), 2, "商用", modPrice.get("MOD-004")),
                item(modId.get("MOD-006"), 1, "个人", modPrice.get("MOD-006")));
        order(3, OrderStatus.Shipped,
                item(modId.get("MOD-002"), 1, "个人", modPrice.get("MOD-002")));
        order(2, OrderStatus.Pending,
                item(modId.get("MOD-006"), 1, "个人", modPrice.get("MOD-006")));
        order(1, OrderStatus.PendingPayment,
                item(modId.get("MOD-003"), 1, "个人", modPrice.get("MOD-003")));
    }

    private void order(int daysAgo, OrderStatus status, OrderItem... items) {
        ModelOrder o = new ModelOrder();
        o.setUserId(demoId);
        o.setOrderDate(LocalDateTime.now().minusDays(daysAgo).minusHours(2));
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem it : items) {
            total = total.add(it.getUnitPrice().multiply(BigDecimal.valueOf(it.getQuantity())));
        }
        o.setTotalAmount(total);
        o.setRecipientName("演示用户");
        o.setRecipientPhone("13800001003");
        o.setRecipientAddress("广东省广州市天河区演示路 1 号");
        o.setStatus(status);
        modelOrderRepository.save(o);
        for (OrderItem it : items) {
            it.setOrderId(o.getOrderId());
            orderItemRepository.save(it);
        }
    }

    private OrderItem item(Long modelId, int quantity, String license, BigDecimal unitPrice) {
        OrderItem i = new OrderItem();
        i.setModelId(modelId);
        i.setQuantity(quantity);
        i.setLicenseType(license);
        i.setUnitPrice(unitPrice);
        return i;
    }

    // ---------- 占位文件生成 ----------

    /** 写入占位文件，返回相对存储路径（type/yyyyMM/uuid.ext） */
    private String writeFile(String type, String fileName, byte[] content) {
        try {
            String subDir = LocalDateTime.now().format(MONTH);
            Path dir = root.resolve(type).resolve(subDir);
            Files.createDirectories(dir);
            Path file = dir.resolve(fileName);
            Files.write(file, content);
            return type + "/" + subDir + "/" + fileName;
        } catch (IOException e) {
            log.warn("[DataInitializer] 占位文件写入失败 {}: {}", fileName, e.getMessage());
            return type + "/" + MONTH.format(LocalDateTime.now()) + "/" + fileName;
        }
    }

    /** 生成渐变 PNG 预览图（按序号变化色相） */
    private byte[] gradientPng(int seed) {
        int w = 640, h = 360;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Color top = Color.getHSBColor((seed * 0.13f) % 1f, 0.55f, 0.75f);
        Color bottom = Color.getHSBColor((seed * 0.13f + 0.15f) % 1f, 0.65f, 0.35f);
        for (int y = 0; y < h; y++) {
            float t = (float) y / h;
            int r = Math.round(top.getRed() * (1 - t) + bottom.getRed() * t);
            int g = Math.round(top.getGreen() * (1 - t) + bottom.getGreen() * t);
            int b = Math.round(top.getBlue() * (1 - t) + bottom.getBlue() * t);
            for (int x = 0; x < w; x++) {
                img.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            log.warn("[DataInitializer] 预览图生成失败: {}", e.getMessage());
            return new byte[0];
        }
    }

    /** 最小合法 PDF（单页空白） */
    private static final byte[] MINIMAL_PDF = (
            "%PDF-1.4\n" +
                    "1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n" +
                    "2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n" +
                    "3 0 obj<</Type/Page/Parent 2 0 R/MediaBox[0 0 612 792]>>endobj\n" +
                    "xref\n0 4\ntrailer<</Size 4/Root 1 0 R>>\n%%EOF\n")
            .getBytes(StandardCharsets.US_ASCII);

    /** ASCII 立方体 OBJ */
    private static final byte[] CUBE_OBJ = (
            "# LearnAI demo cube\n" +
                    "v -1 1 1\nv -1 -1 1\nv 1 -1 1\nv 1 1 1\n" +
                    "v -1 1 -1\nv -1 -1 -1\nv 1 -1 -1\nv 1 1 -1\n" +
                    "f 1 2 3 4\nf 5 8 7 6\nf 1 5 6 2\nf 2 6 7 3\nf 3 7 8 4\nf 5 1 4 8\n")
            .getBytes(StandardCharsets.US_ASCII);
}
