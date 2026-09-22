package org.example.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.web.entity.PostLike;

@Mapper
public interface PostLikeMapper {

    /**
     * 查重：统计某用户对某帖子的点赞记录条数
     * <p>调用方式：if (mapper.countByPostIdAndUserId(postId, userId) > 0) { 已点赞 }</p>
     * <p>post_like 表有 UNIQUE(post_id, user_id) 约束，同一人对同一帖子
     *    最多 1 条记录，所以返回值只会是 0 或 1</p>
     *
     * @param postId 帖子 ID
     * @param userId 点赞人学号
     * @return 0 = 未点赞；1 = 已点赞
     */
    int countByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 插入一条点赞记录（id 由数据库自增，created_at 由表的 DEFAULT CURRENT_TIMESTAMP 自动填充）
     * <p>并发重复点赞时命中 UNIQUE(post_id, user_id) 约束，
     *    MyBatis 会向上抛 DuplicateKeyException，由调用方捕获后按"已点赞"处理</p>
     *
     * @param postLike 只需设置 postId 和 userId 两个字段
     * @return 1 = 插入成功；0 = 失败
     */
    int insert(PostLike postLike);

    /**
     * 取消点赞：删除该用户对该帖子的点赞记录
     *
     * @param postId 帖子 ID
     * @param userId 点赞人学号
     * @return 1 = 删除成功（之前确实点过赞）；0 = 没有记录（本来就没点过）
     */
    int deleteByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);
}
