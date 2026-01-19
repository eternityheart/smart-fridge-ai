package com.smartfridge.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartfridge.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);
}
