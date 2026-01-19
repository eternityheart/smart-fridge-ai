package com.smartfridge.task;

import com.smartfridge.domain.entity.Inventory;
import com.smartfridge.domain.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定时任务
 * 扫描即将过期和已过期的食材
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExpirationScanTask {

    private final InventoryMapper inventoryMapper;

    /**
     * 每天早上 8 点扫描即将过期的食材 (未来 3 天内)
     * 给用户发送提醒通知
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void scanExpiringSoon() {
        log.info("开始扫描即将过期食材...");

        // 扫描未来 3 天内过期的食材
        LocalDate threshold = LocalDate.now().plusDays(3);
        List<Inventory> expiringItems = inventoryMapper.selectAllExpiringSoon(threshold);

        if (expiringItems.isEmpty()) {
            log.info("暂无即将过期的食材");
            return;
        }

        // 按用户分组
        Map<Long, List<Inventory>> userItems = expiringItems.stream()
                .collect(Collectors.groupingBy(Inventory::getUserId));

        // 发送通知 (模拟)
        userItems.forEach((userId, items) -> {
            String itemNames = items.stream()
                    .map(item -> item.getIngredientName() + "(" + item.getExpireDate() + ")")
                    .collect(Collectors.joining(", "));

            // TODO: 调用消息推送服务 (WebSocket/邮件/短信)
            log.info("[通知] 用户 {} 的以下食材即将过期: {}", userId, itemNames);
        });

        log.info("即将过期食材扫描完成，共通知 {} 位用户", userItems.size());
    }

    /**
     * 每天凌晨 1 点清理已过期超过 7 天的食材
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanupExpired() {
        log.info("开始清理过期食材...");

        // TODO: 实现过期食材自动清理逻辑
        // 暂时只打印日志

        log.info("过期食材清理完成");
    }

    /**
     * 每 30 分钟检查系统状态 (用于监控)
     */
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void healthCheck() {
        log.debug("系统健康检查 - 运行正常");
    }
}
