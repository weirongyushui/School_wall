package org.example.web.mapper;

import org.example.web.entity.Login;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录账号 Mapper
 * 对应数据库 login 表，SQL 定义在 resources/mybatis/loginMapper.xml。
 * 提供 login 表的增删改查接口，供 LoginService 使用。
 */
@Mapper
public interface LoginMapper {
    /** 按主键 ID 查询账号 */
    Login getById(Long id);
    /** 按用户名查询账号（登录时使用） */
    Login getByUsername(String username);
    /** 按学号（login.user_id）查询账号（学号登录时使用） */
    Login getByUserId(Long userId);
    /** 插入账号，自动回填 id */
    int insert(Login login);
    /** 动态更新账号字段（username/password/user_id） */
    int update(Login login);
    /** 按主键物理删除账号 */
    int deleteById(Long id);
}
