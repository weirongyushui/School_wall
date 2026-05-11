package org.example.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.web.entity.Post;

import java.util.List;
import java.util.Map;

/**
 * 动态广场 Mapper
 */
@Mapper
public interface SocialMapper {

    /**
     * 根据 ID 查询帖子
     * @param id 帖子 ID
     * @return 帖子实体
     */
    Post getById(@Param("id") Long id);

    /**
     * 查询帖子列表（分页）
     * @param params 参数：userId, isAnonymous, offset, limit
     * @return 帖子列表
     */
    List<Post> getList(Map<String, Object> params);

    /**
     * 查询帖子总数
     * @param params 参数：userId, isAnonymous
     * @return 总数
     */
    int getCount(Map<String, Object> params);

    /**
     * 根据用户 ID 查询帖子
     * @param userId 用户 ID
     * @return 帖子列表
     */
    List<Post> getByUserId(@Param("userId") Long userId);

    /**
     * 插入帖子
     * @param post 帖子实体
     * @return 影响行数
     */
    int insert(Post post);

    /**
     * 更新帖子
     * @param post 帖子实体
     * @return 影响行数
     */
    int update(Post post);

    /**
     * 删除帖子（软删除）
     * @param id 帖子 ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 更新点赞数
     * @param postId 帖子 ID
     * @param delta 变化量（+1 或 -1）
     * @return 影响行数
     */
    int updateLikeCount(@Param("postId") Long postId, @Param("delta") int delta);

    /**
     * 更新评论数
     * @param postId 帖子 ID
     * @param delta 变化量（+1 或 -1）
     * @return 影响行数
     */
    int updateCommentCount(@Param("postId") Long postId, @Param("delta") int delta);

    /**
     * 批量查询帖子
     * @param ids 帖子 ID 列表
     * @return 帖子列表
     */
    List<Post> getBatch(@Param("list") List<Long> ids);

    /**
     * 审核通过帖子
     * @param id 帖子 ID
     * @return 影响行数
     */
    int approvePost(@Param("id") Long id);

    /**
     * 审核拒绝/屏蔽帖子
     * @param id 帖子 ID
     * @return 影响行数
     */
    int rejectPost(@Param("id") Long id);

    /**
     * 查询待审核帖子列表（分页）
     * @param params 参数：offset, limit
     * @return 帖子列表
     */
    List<Post> getPendingList(Map<String, Object> params);

    /**
     * 查询待审核帖子总数
     * @return 总数
     */
    int getPendingCount();
}
