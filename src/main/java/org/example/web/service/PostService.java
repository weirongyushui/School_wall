package org.example.web.service;

import java.util.Map;

/**
 * 帖子服务接口
 */
public interface PostService {

    /**
     * 发布帖子
     * @param userId      发布者 ID（从会话获取，对应 login.user_id）
     * @param content     帖子内容
     * @param category    分区（1=发发牢骚, 2=吹吹牛皮, 3=校园互助, 4=联名请愿）
     * @param isAnonymous 是否匿名（1=匿名, 0=实名）
     * @return 错误消息，null 表示成功
     */
    String createPost(Long userId, String content, Integer category, Integer isAnonymous);

    /**
     * 查询帖子列表（分页 + 分类筛选）
     * @param page     页码，从 1 开始
     * @param size     每页数量
     * @param category 分类筛选（null 或 0 表示全部）
     * @return "list" → List<Post>, "total" → int
     */
    Map<String, Object> getPostList(int page, int size, Integer category);

    /**
     * TODO: 点赞帖子
     * @param userId 用户 ID
     * @param postId 帖子 ID
     * @return 错误消息，null 表示成功
     */
    // String likePost(Long userId, Long postId);

    /**
     * TODO: 获取热门帖子（按点赞数排序）
     * @param limit 返回数量
     * @return 热门帖子列表
     */
    // List<Post> getHotPosts(int limit);

    /**
     * 删除帖子（软删除）
     * @param userId 用户 ID（验证权限）
     * @param postId 帖子 ID
     * @return 错误消息，null 表示成功
     */
    String deletePost(Long userId, Long postId);

    /**
     * TODO: 更新帖子
     * @param userId 用户 ID（验证权限）
     * @param postId 帖子 ID
     * @param content 新内容
     * @return 错误消息，null 表示成功
     */
    // String updatePost(Long userId, Long postId, String content);
}
