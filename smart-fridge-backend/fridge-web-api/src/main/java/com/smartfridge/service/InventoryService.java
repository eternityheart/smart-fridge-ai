package com.smartfridge.service;

import com.smartfridge.domain.entity.Inventory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 库存服务接口
 */
public interface InventoryService {

    /**
     * 处理冰箱照片快照
     * 调用视觉服务识别食材，更新库存
     *
     * @param userId 用户ID
     * @param image  冰箱照片
     * @return 识别结果和更新后的库存
     */
    Map<String, Object> processSnapshot(Long userId, MultipartFile image);

    /**
     * 获取用户的所有库存
     */
    List<Inventory> getByUserId(Long userId);

    /**
     * 获取即将过期的食材（3天内）
     */
    List<Inventory> getExpiringSoon(Long userId);

    /**
     * 获取已过期的食材
     */
    List<Inventory> getExpired(Long userId);

    /**
     * 添加食材
     */
    Inventory add(Inventory inventory);

    /**
     * 更新食材
     */
    Inventory update(Inventory inventory);

    /**
     * 删除食材
     */
    void delete(Long id);
}
