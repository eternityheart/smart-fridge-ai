package com.smartfridge.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartfridge.domain.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 库存 Mapper
 */
@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {

    /**
     * 查询用户的所有库存
     */
    @Select("SELECT * FROM inventory WHERE user_id = #{userId} ORDER BY expire_date ASC")
    List<Inventory> selectByUserId(Long userId);

    /**
     * 查询即将过期的食材
     */
    @Select("SELECT * FROM inventory WHERE user_id = #{userId} AND expire_date BETWEEN CURDATE() AND #{date}")
    List<Inventory> selectExpiringSoon(Long userId, LocalDate date);

    /**
     * 查询已过期的食材
     */
    @Select("SELECT * FROM inventory WHERE user_id = #{userId} AND expire_date < CURDATE()")
    List<Inventory> selectExpired(Long userId);
}
