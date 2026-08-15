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
        String sidebarPosition,
        String animationSpeed
) {
    public static PreferencesDto from(User u) {
        return new PreferencesDto(
                u.getFontSize(), u.getBorderColor(), u.getThemeColor(),
                u.getDarkMode(), u.getSidebarPosition(), u.getAnimationSpeed());
    }

    public void applyTo(User u) {
        u.setFontSize(fontSize);
        u.setBorderColor(borderColor);
        u.setThemeColor(themeColor);
        u.setDarkMode(darkMode);
        u.setSidebarPosition(sidebarPosition);
        u.setAnimationSpeed(animationSpeed);
    }
}
