package com.smartfridge.controller;

import com.smartfridge.common.result.Result;
import com.smartfridge.domain.entity.Inventory;
import com.smartfridge.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 冰箱管理控制器
 */
@Tag(name = "冰箱管理", description = "冰箱拍照识别、库存管理")
@RestController
@RequestMapping("/api/fridge")
@RequiredArgsConstructor
public class FridgeController {

    private final InventoryService inventoryService;

    /**
     * 上传冰箱照片进行识别
     */
    @Operation(summary = "拍照识别", description = "上传冰箱照片，识别食材并更新库存")
    @PostMapping("/snapshot")
    public Result<Map<String, Object>> uploadSnapshot(
            @Parameter(description = "冰箱照片") @RequestParam("image") MultipartFile image,
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        
        Map<String, Object> result = inventoryService.processSnapshot(userId, image);
        return Result.success("识别成功", result);
    }

    /**
     * 获取当前库存列表
     */
    @Operation(summary = "获取库存", description = "获取用户冰箱中的所有食材")
    @GetMapping("/inventory")
    public Result<List<Inventory>> getInventory(
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        
        List<Inventory> list = inventoryService.getByUserId(userId);
        return Result.success(list);
    }

    /**
     * 获取即将过期的食材
     */
    @Operation(summary = "即将过期", description = "获取3天内即将过期的食材")
    @GetMapping("/inventory/expiring")
    public Result<List<Inventory>> getExpiring(
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId) {
        
        List<Inventory> list = inventoryService.getExpiringSoon(userId);
        return Result.success(list);
    }

    /**
     * 手动添加食材
     */
    @Operation(summary = "添加食材", description = "手动添加食材到库存")
    @PostMapping("/inventory")
    public Result<Inventory> addInventory(
            @Parameter(description = "用户ID") @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody Inventory inventory) {
        
        inventory.setUserId(userId);
        inventory.setEntrySource("MANUAL");
        Inventory saved = inventoryService.add(inventory);
        return Result.success("添加成功", saved);
    }

    /**
     * 更新食材信息
     */
    @Operation(summary = "更新食材", description = "修改食材信息（名称、数量、过期日期等）")
    @PutMapping("/inventory/{id}")
    public Result<Inventory> updateInventory(
            @Parameter(description = "库存ID") @PathVariable Long id,
            @Valid @RequestBody Inventory inventory) {
        
        inventory.setId(id);
        Inventory updated = inventoryService.update(inventory);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除食材
     */
    @Operation(summary = "删除食材", description = "从库存中移除食材")
    @DeleteMapping("/inventory/{id}")
    public Result<Void> deleteInventory(
            @Parameter(description = "库存ID") @PathVariable Long id) {
        
        inventoryService.delete(id);
        return Result.success("删除成功", null);
    }
}
