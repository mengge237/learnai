package com.learnai.dto.study;

/**
 * 学习心跳上报：学习页定时上报实际学习时长（秒）。
 */
public record HeartbeatRequest(
        Long resourceId,
        Integer seconds
) {
}
