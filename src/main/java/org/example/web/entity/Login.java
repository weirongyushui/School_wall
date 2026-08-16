package org.example.web.entity;

import java.util.Date;

/**
 * 登录账号实体
 * 对应数据库 login 表（resources/mybatis/loginMapper.xml）。
 * 字段与 login 表一一对应：id / username / password / user_id / created_at / updated_at。
 * 其中 user_id 为外键，指向 user.user_id（学号）。
 */
public class Login {
    /**
     * 主键 id
     */
    private Long id;
    /**
     * 用户名（唯一索引）
     */
    private String username;
    /**
     * 密码（新数据为 MD5，旧数据可能为明文）
     */
    private String password;
    /**
     * 学号（外键 → user.user_id）
     */
    private Long userId;

    private String role;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 更新时间
     */
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }


    @Override
    public String toString() {
        return "Login{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

}
