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
}
