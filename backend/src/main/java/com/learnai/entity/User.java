package com.learnai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户（含界面个性化设置字段）
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /** 学号（校园特供版，选填） */
    @Column(name = "student_no", length = 30)
    private String studentNo;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private UserRole role;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "province", length = 50)
    private String province;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "default_shipping_address", length = 200)
    private String defaultShippingAddress;

    // ---------- 界面个性化设置 ----------

    @Column(name = "font_size", nullable = false)
    private Integer fontSize = 16;

    @Column(name = "border_color", nullable = false, length = 20)
    private String borderColor = "#d0d0d0";

    @Column(name = "theme_color", nullable = false, length = 20)
    private String themeColor = "#e8590c";

    @Column(name = "dark_mode", nullable = false)
    private Boolean darkMode = false;

    /** 外观模式：light / dark / auto（跟随系统） */
    @Column(name = "theme_mode", nullable = false, length = 10)
    private String themeMode = "auto";

    @Column(name = "sidebar_position", nullable = false, length = 10)
    private String sidebarPosition = "left";

    @Column(name = "animation_speed", nullable = false, length = 10)
    private String animationSpeed = "normal";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
