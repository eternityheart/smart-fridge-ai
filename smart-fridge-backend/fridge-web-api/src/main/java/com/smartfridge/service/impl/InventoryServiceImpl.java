package com.smartfridge.service.impl;

import com.smartfridge.common.exception.BusinessException;
import com.smartfridge.common.result.ResultCode;
import com.smartfridge.domain.entity.Inventory;
import com.smartfridge.domain.mapper.InventoryMapper;
import com.smartfridge.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

/**
 * 库存服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;
    // TODO: 注入 VisionService 和 LLMService

    @Override
    @Transactional
    public Map<String, Object> processSnapshot(Long userId, MultipartFile image) {
        // 验证文件
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ResultCode.FILE_EMPTY);
        }

        // TODO: 1. 上传图片到 MinIO
        // TODO: 2. 调用视觉服务识别食材
        // TODO: 3. 调用 DeepSeek 清洗数据
        // TODO: 4. 更新库存

        // 模拟返回（后续接入真实服务）
        Map<String, Object> result = new HashMap<>();
        result.put("imageUrl", "http://localhost:9000/fridge-images/demo.jpg");
        result.put("recognizedCount", 0);
        result.put("items", Collections.emptyList());
        result.put("message", "视觉服务待接入");

        log.info("处理用户 {} 的冰箱快照", userId);
        return result;
    }

    @Override
    public List<Inventory> getByUserId(Long userId) {
        return inventoryMapper.selectByUserId(userId);
    }

    @Override
    public List<Inventory> getExpiringSoon(Long userId) {
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);
        return inventoryMapper.selectExpiringSoon(userId, threeDaysLater);
    }

    @Override
    public List<Inventory> getExpired(Long userId) {
        return inventoryMapper.selectExpired(userId);
    }

    @Override
    @Transactional
    public Inventory add(Inventory inventory) {
        inventoryMapper.insert(inventory);
        log.info("添加食材: {}", inventory.getIngredientName());
        return inventory;
    }

    @Override
    @Transactional
    public Inventory update(Inventory inventory) {
        Inventory existing = inventoryMapper.selectById(inventory.getId());
        if (existing == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        inventoryMapper.updateById(inventory);
        log.info("更新食材: id={}", inventory.getId());
        return inventoryMapper.selectById(inventory.getId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int rows = inventoryMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        log.info("删除食材: id={}", id);
    }
}
