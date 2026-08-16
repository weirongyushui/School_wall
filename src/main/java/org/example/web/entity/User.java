package org.example.web.entity;

import jakarta.persistence.*;

import java.util.Date;

/**
 * 用户资料实体
 * 对应数据库 user 表，使用 JPA 注解（@Entity/@Table）。
 * 同时供 MyBatis 使用（userMapper.xml 中 ResultMap 直接映射字段名）。
 * 特别说明：getUserId/setUserId 与 getUser_id/setUser_id 同时存在，
 *           是为兼容 MyBatis property 与 Jackson 序列化字段名。
 */
@Entity
@Table(name = "user")
public class User {
    /** 主键 id（自增） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** 学号（唯一索引，业务上的用户标识） */
    private String userId;
    /** 昵称 */
    private String nickname;
    /** 头像 URL */
    private String avatar;
    /** 性别：1=男，0=女，-1=未设置（注册时默认 -1） */
    private Integer gender;
    /** 专业 */
    private String major;
    /** 年级 */
    private String grade;
    /** 个人简介 */
    private String introduction;
    /** 电话 */
    private String phone;
    /** 邮箱 */
    private String email;
    /** QQ */
    private String qq;
    /** 微信 */
    private String wechat;
    /** 地址 */
    private String address;
    /** 生日 */
    private java.util.Date birthday;
    /** 创建时间 */
    private java.util.Date createdAt;
    /** 更新时间 */
    private java.util.Date updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /** 标准 JavaBean 名，供 MyBatis（property=userId、#{userId}）与 Jackson 使用。 */
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUser_id() {
        return userId;
    }

    public void setUser_id(String user_id) {
        this.userId = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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
        return "User{" +
                "id=" + id +
                ", user_id='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender=" + gender +
                ", major='" + major + '\'' +
                ", grade='" + grade + '\'' +
                ", introduction='" + introduction + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", qq='" + qq + '\'' +
                ", wechat='" + wechat + '\'' +
                ", address='" + address + '\'' +
                ", birthday=" + birthday +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
