package org.example.web.entity;

import java.util.Date;

/**
 * 评论实体类
 * TODO: 完善字段定义
 */
public class Comment {

    /** TODO: 评论 ID */
    private Long id;

    /** TODO: 帖子 ID */
    private Long postId;

    /** TODO: 用户 ID */
    private Long userId;

    /** TODO: 用户名 */
    private String username;

    /** TODO: 头像 */
    private String avatar;

    /** TODO: 是否匿名 */
    private Integer isAnonymous;

    /** TODO: 评论内容 */
    private String content;

    /** TODO: 状态（1=正常，0=已删除） */
    private Integer status;

    /** TODO: 点赞数 */
    private Integer likeCount;

    /** TODO: 回复的评论 ID（回复功能） */
    private Long replyId;

    /** TODO: 创建时间 */
    private Date createdAt;

    /** TODO: 更新时间 */
    private Date updatedAt;

    // TODO: 添加 getter 和 setter 方法
}
