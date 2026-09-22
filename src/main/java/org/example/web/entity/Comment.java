package org.example.web.entity;

import java.util.Date;

/**
 * 评论实体类
 * 对应 comment 表（TODO: 建表与 Mapper/Service 待实现）
 */
public class Comment {

    /** 评论 ID */
    private Long id;

    /** 所属帖子 ID */
    private Long postId;

    /** 评论人学号 */
    private Long userId;

    /** 评论人昵称（冗余字段） */
    private String username;

    /** 评论人头像（冗余字段） */
    private String avatar;

    /** 是否匿名：1=匿名，0=实名 */
    private Integer isAnonymous;

    /** 评论内容 */
    private String content;

    /** 状态：1=正常，0=已删除 */
    private Integer status;

    /** 点赞数 */
    private Integer likeCount;

    /** 回复的目标评论 ID（顶级评论为 null） */
    private Long replyId;

    /** 创建时间 */
    private Date createdAt;

    /** 更新时间 */
    private Date updatedAt;

    // TODO: 添加 getter 和 setter 方法
}
