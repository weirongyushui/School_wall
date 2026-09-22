package org.example.web.service;

/**
 * 帖子点赞服务接口
 */
public interface PostLikeService {

    /**
     * 点赞（幂等，重复点赞不重复计数）
     * @param userId 点赞人学号
     * @param postId 帖子 ID
     * @return true=点赞成功；false=已点过赞
     */
    boolean like(Long userId, Long postId);

    /**
     * 取消点赞（幂等）
     * @param userId 点赞人学号
     * @param postId 帖子 ID
     * @return true=取消成功；false=未点过赞
     */
    boolean unlike(Long userId, Long postId);

    /**
     * 查询用户是否已点赞某帖子
     * @param userId 用户学号
     * @param postId 帖子 ID
     * @return true=已点赞
     */
    boolean hasLiked(Long userId, Long postId);

    /**
     * 查询正常帖子的最新点赞数。
     * @param postId 帖子 ID
     * @return 当前点赞数
     * @throws IllegalArgumentException 帖子不存在或参数不合法
     */
    int getLikeCount(Long postId);
}
