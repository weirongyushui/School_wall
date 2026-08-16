package org.example.web.entity;

import java.util.Date;

/**
 * 帖子实体类
 * 对应 post 表，字段与 socialMapper.xml 的 BaseResultMap 一一对应
 */
public class Post {

    /** 主键 id */
    private Long id;
    /** 帖子正文 */
    private String content;
    /** 图片 URL（逗号分隔多图，当前无写入逻辑） */
    private String images;
    /** 发布者学号（对应 user.user_id） */
    private Long userId;
    /** 发布者用户名（冗余字段，发帖时写入） */
    private String username;
    /** 发布者头像（冗余字段，发帖时写入） */
    private String avatar;
    /** 是否匿名：0=实名，1=匿名 */
    private Integer isAnonymous;
    /** 分类：0=全部，1=发发牢骚，2=吹吹牛皮，3=校园互助，4=联名请愿 */
    private Integer category;
    /** 状态：0=已删除，1=正常，2=已屏蔽/待审核 */
    private Integer status;
    /** 点赞数（无点赞业务调用，恒为 0） */
    private Integer likeCount;
    /** 评论数（无评论业务调用，恒为 0） */
    private Integer commentCount;
    /** 创建时间 */
    private Date createdAt;
    /** 更新时间 */
    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Integer isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
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
        return "Post{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", images='" + images + '\'' +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", isAnonymous=" + isAnonymous +
                ", category=" + category +
                ", status=" + status +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
