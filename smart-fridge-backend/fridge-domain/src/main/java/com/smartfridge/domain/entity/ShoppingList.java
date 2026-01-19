package com.smartfridge.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物清单实体
 */
@Data
@TableName("shopping_list")
public class ShoppingList {

    /**
     * 清单ID
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
     * 是否已购买
     */
    private Boolean purchased;

    /**
     * 来源菜谱
     */
    private String sourceRecipe;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
