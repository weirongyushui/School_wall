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
     * TODO(P1-帖子详情): 根据 postId 查询一条正常帖子，并组装匿名安全字段、canDelete、liked。
     * 评论列表建议由 CommentService 分页查询，避免帖子接口一次返回过多回复。
     */
    // Map<String, Object> getPostDetail(Long postId, Long currentUserId);

    /**
     * TODO(P1-我的帖子): 复用列表分页逻辑，在查询参数中加入当前 userId；userId 必须取自 Session，
     * 不能接收前端任意传入的学号，否则会变成查询他人匿名帖的入口。
     */
    // Map<String, Object> getMyPosts(Long currentUserId, int page, int size);

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

    /**
     * TODO(P1-多图发布): 图片上传与发帖分两步：上传接口只返回受控的文件标识/URL，createPost 再保存图片列表。
     * Service 需要限制图片数量，并只接受本站上传成功的标识，不能直接信任任意外链。
     */
}
