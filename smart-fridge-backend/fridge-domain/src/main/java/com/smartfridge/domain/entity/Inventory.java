package com.smartfridge.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 库存实体 (冰箱食材)
 */
@Data
@TableName("inventory")
public class Inventory {

    /**
     * 库存ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 食材名称
     */
    private String ingredientName;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 单位
     */
    private String unit;

    /**
     * 过期日期
     */
    private LocalDate expireDate;

    /**
     * 录入来源: PHOTO=拍照识别, MANUAL=手动录入, BARCODE=条码扫描
     */
    private String entrySource;

    /**
     * 识别置信度 (0-1)
     */
    private BigDecimal confidence;

    /**
     * 原始图片URL
     */
    private String imageUrl;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // ==================== 业务方法 ====================

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        return expireDate != null && expireDate.isBefore(LocalDate.now());
    }

    /**
     * 是否即将过期 (3天内)
     */
    public boolean isExpiringSoon() {
        if (expireDate == null) {
            return false;
        }
        LocalDate threeDaysLater = LocalDate.now().plusDays(3);
        return expireDate.isBefore(threeDaysLater) && !isExpired();
    }
}
