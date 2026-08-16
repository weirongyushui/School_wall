package org.example.web.mapper;

import org.example.web.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户资料 Mapper
 * 对应数据库 user 表，SQL 定义在 resources/mybatis/userMapper.xml。
 * 提供 user 表的增删改查接口，供 LoginService 与 UserProfileService 使用。
 */
@Mapper
public interface UserMapper {
    /** 按主键 id 查询用户 */
    User getById(Long id);
    /** 按学号（user.user_id）查询用户 */
    User getByUserId(String userId);
    /**
     * 按主键 id 查询用户（方法名小写 y，与 getById 行为相同）
     * 注：保留用于兼容历史调用，新代码建议使用 getById
     */
    User getByuserId(Integer id);
    /** 插入用户，自动回填 id */
    int insert(User user);
    /** 动态更新用户字段（含 user_id） */
    int update(User user);
    /** 按主键物理删除用户 */
    int deleteById(Long id);
}
