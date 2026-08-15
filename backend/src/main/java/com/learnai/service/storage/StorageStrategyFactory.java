package com.learnai.service.storage;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 存储策略工厂（工厂模式）：把全部 StorageStrategy 实现注册到
 * 名称 → 策略 的注册表，按名称取用；扩展对象存储时新增实现即可，调用方无感。
 */
@Component
public class StorageStrategyFactory {

    private final Map<String, StorageStrategy> registry;

    public StorageStrategyFactory(List<StorageStrategy> strategies) {
        this.registry = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(StorageStrategy::name, Function.identity()));
    }

    /** 默认策略：本地磁盘 */
    public StorageStrategy defaultStrategy() {
        return strategy("local");
    }

    /** 按名称取策略 */
    public StorageStrategy strategy(String name) {
        StorageStrategy s = registry.get(name);
        if (s == null) {
            throw new IllegalStateException("未注册的存储策略: " + name);
        }
        return s;
    }
}
