package com.learnai.dto.auth;

import com.learnai.entity.User;

/**
 * 界面个性化设置
 */
public record PreferencesDto(
        Integer fontSize,
        String borderColor,
        String themeColor,
        Boolean darkMode,
        String themeMode,
        String sidebarPosition,
        String animationSpeed
) {
    public static PreferencesDto from(User u) {
        return new PreferencesDto(
                u.getFontSize(), u.getBorderColor(), u.getThemeColor(),
                u.getDarkMode(), u.getThemeMode(), u.getSidebarPosition(), u.getAnimationSpeed());
    }

    public void applyTo(User u) {
        if (fontSize != null) u.setFontSize(fontSize);
        if (borderColor != null) u.setBorderColor(borderColor);
        if (themeColor != null) u.setThemeColor(themeColor);
        if (darkMode != null) u.setDarkMode(darkMode);
        if (themeMode != null) u.setThemeMode(themeMode);
        if (sidebarPosition != null) u.setSidebarPosition(sidebarPosition);
        if (animationSpeed != null) u.setAnimationSpeed(animationSpeed);
    }
}
