package org.example.web.service;

import org.example.web.entity.PostLike;
import org.example.web.mapper.PostLikeMapper;
import org.example.web.mapper.SocialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeServiceImpl implements PostLikeService {
    @Autowired
    private PostLikeMapper postLikeMapper;
    @Autowired
    private SocialMapper socialMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean like(Long userId, Long postId) {
        // 实现思路：
        // 1. userId/postId 不能为空，并用 socialMapper.getById(postId) 确认帖子正常存在；
        // 2. 先查 countByPostIdAndUserId，已点赞直接返回 false，接口保持幂等；
        // 3. 插入 PostLike 后调用 updateLikeCount(postId, +1)；任一步失败都抛异常触发回滚；
        // 4. 数据库必须建立 UNIQUE(post_id, user_id)，防止并发请求绕过第 2 步重复点赞。

        // 1. 参数校验 + 确认帖子正常存在（getById 只查 status=1 的帖子）
        if (userId == null || postId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (socialMapper.getById(postId) == null) {
            throw new IllegalArgumentException("帖子不存在");
        }

        // 2. 幂等检查：已点赞直接返回 false，不插入、不计数
        if (postLikeMapper.countByPostIdAndUserId(postId, userId) > 0) {
            return false;
        }

        // 3. 插入点赞记录，成功后帖子点赞数 +1
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        try {
            postLikeMapper.insert(postLike);
        } catch (DuplicateKeyException e) {
            // 4. 并发重复请求命中 UNIQUE(post_id, user_id)，按"已点赞"幂等处理
            return false;
        }

        // 更新 0 行说明帖子状态异常，抛异常回滚上面的插入
        if (socialMapper.updateLikeCount(postId, 1) == 0) {
            throw new IllegalStateException("点赞失败");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlike(Long userId, Long postId) {
        // 1. 参数校验
        if (userId == null || postId == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 2. 先删除 (postId,userId) 点赞记录：
        //    删除 0 行 = 本来就没点过，直接返回 false（幂等），不扣计数
        //    删除 1 行 = 确实点过，继续把 like_count 减 1
        int deleted = postLikeMapper.deleteByPostIdAndUserId(postId, userId);
        if (deleted == 0) {
            return false;
        }

        // 3. 点赞数 -1；updateLikeCount 的 SQL 带 like_count + delta >= 0 保护，不会减成负数
        //    更新 0 行说明帖子状态异常，抛异常回滚上面的删除
        if (socialMapper.updateLikeCount(postId, -1) == 0) {
            throw new IllegalStateException("取消点赞失败");
        }
        return true;
    }

    @Override
    public boolean hasLiked(Long userId, Long postId) {
        // 未登录或没有目标帖子时，统一视为未点赞，便于列表和详情页直接展示默认状态。
        if (userId == null || postId == null) {
            return false;
        }
        return postLikeMapper.countByPostIdAndUserId(postId, userId) > 0;
    }

    @Override
    public int getLikeCount(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("帖子 ID 不能为空");
        }
        var post = socialMapper.getById(postId);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        return post.getLikeCount() == null ? 0 : post.getLikeCount();
    }
}
