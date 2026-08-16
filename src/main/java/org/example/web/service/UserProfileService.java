package org.example.web.service;

import org.example.web.entity.User;
import org.example.web.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户资料服务
 * 负责用户资料的增删改查。依赖 UserMapper（user 表）。
 * saveUserProfile 实现了"存在则更新、不存在则新增"的 upsert 逻辑。
 */
@Service
public class UserProfileService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 按主键 ID 查询用户
     * @param id user.id
     * @return User 实体或 null
     */
    public User getById(Long id){
        return userMapper.getById(id);
    }

    /**
     * 按学号查询用户
     * @param userId 学号字符串（对应 user.user_id）
     * @return User 实体或 null
     */
    public User getByUserId(String userId){
        return userMapper.getByUserId(userId);
    }

    /**
     * 保存用户资料（upsert）
     * - 若 id>0 且数据库存在该记录：执行 update 后回查返回
     * - 否则：id 置 0、设置 createdAt，执行 insert 后回查返回
     * @param user 待保存的 User 实体
     * @return 保存后的最新 User 实体
     */
    public User saveUserProfile(User user){
        Date now = new Date();
        user.setUpdatedAt(now);

        // 主键大于 0 视为更新场景
        if (user.getId() > 0) {
            User existed = userMapper.getById(user.getId());
            if (existed != null) {
                userMapper.update(user);
                return userMapper.getById(user.getId());
            }
        }

        // 否则走 insert，主键由数据库自增
        user.setId(0);
        user.setCreatedAt(now);
        userMapper.insert(user);
        return userMapper.getById(user.getId());
    }

    /**
     * 插入用户
     * @param user User 实体
     * @return 影响行数
     */
    public int insert(User user){
        return userMapper.insert(user);
    }

    /**
     * 更新用户
     * @param user User 实体
     * @return 影响行数
     */
    public int update(User user){
        return userMapper.update(user);
    }

    /**
     * 按主键物理删除用户
     * @param id user.id
     * @return 影响行数
     */
    public int deleteById(Long id){
        return userMapper.deleteById(id);
    }
}
