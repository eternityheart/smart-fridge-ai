package com.smartfridge.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartfridge.domain.entity.ShoppingList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 购物清单 Mapper
 */
@Mapper
public interface ShoppingListMapper extends BaseMapper<ShoppingList> {

    /**
     * 查询用户的购物清单
     */
    @Select("SELECT * FROM shopping_list WHERE user_id = #{userId} AND purchased = FALSE ORDER BY created_at DESC")
    List<ShoppingList> selectUnpurchasedByUserId(Long userId);

    /**
     * 查询用户的所有购物清单
     */
    @Select("SELECT * FROM shopping_list WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<ShoppingList> selectByUserId(Long userId);
}
