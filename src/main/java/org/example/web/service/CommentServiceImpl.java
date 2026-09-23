package org.example.web.service;

import org.example.web.entity.Comment;
import org.example.web.entity.Post;
import org.example.web.entity.User;
import org.example.web.mapper.CommentMapper;
import org.example.web.mapper.SocialMapper;
import org.example.web.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final SocialMapper socialMapper;
    private final UserMapper userMapper;

    public CommentServiceImpl(CommentMapper commentMapper,
                              SocialMapper socialMapper,
                              UserMapper userMapper) {
        this.commentMapper = commentMapper;
        this.socialMapper = socialMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Map<String, Object> getTopLevelComments(Long currentUserId, Long postId, int page, int size) {
        requireNormalPost(postId);
        int safePage = normalizePage(page);
        int safeSize = normalizeSize(size);
        List<Comment> comments = commentMapper.getTopLevelList(
                postId, (safePage - 1) * safeSize, safeSize);
        return pageResult(comments, commentMapper.getTopLevelCount(postId), safePage, safeSize, currentUserId);
    }

    @Override
    public Map<String, Object> getReplies(Long currentUserId, Long parentId, int page, int size) {
        Comment parent = requireNormalComment(parentId);
        if (parent.getParentId() != null) {
            throw new IllegalArgumentException("只能查询一级评论下的回复");
        }
        int safePage = normalizePage(page);
        int safeSize = normalizeSize(size);
        List<Comment> replies = commentMapper.getReplyList(
                parentId, (safePage - 1) * safeSize, safeSize);
        return pageResult(replies, commentMapper.getReplyCount(parentId), safePage, safeSize, currentUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createComment(Long userId, Long postId, String content, Integer isAnonymous) {
        requireUserId(userId);
        requireNormalPost(postId);
        Comment comment = buildComment(userId, postId, content, isAnonymous);
        commentMapper.insert(comment);
        if (socialMapper.updateCommentCount(postId, 1) == 0) {
            throw new IllegalStateException("评论数更新失败");
        }
        return toView(comment, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> createReply(Long userId, Long targetCommentId,
                                           String content, Integer isAnonymous) {
        requireUserId(userId);
        Comment target = requireNormalComment(targetCommentId);
        requireNormalPost(target.getPostId());

        Long parentId = target.getParentId() == null ? target.getId() : target.getParentId();
        Comment parent = requireNormalComment(parentId);
        if (parent.getParentId() != null || !parent.getPostId().equals(target.getPostId())) {
            throw new IllegalArgumentException("回复关系无效");
        }

        Comment reply = buildComment(userId, target.getPostId(), content, isAnonymous);
        reply.setParentId(parentId);
        reply.setReplyToCommentId(target.getId());
        commentMapper.insert(reply);

        if (commentMapper.updateReplyCount(parentId, 1) == 0
                || socialMapper.updateCommentCount(target.getPostId(), 1) == 0) {
            throw new IllegalStateException("回复数量更新失败");
        }
        return toView(reply, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteComment(Long userId, Long commentId) {
        requireUserId(userId);
        Comment comment = requireNormalComment(commentId);
        if (!userId.equals(comment.getUserId())) {
            throw new IllegalArgumentException("只能删除自己的评论");
        }
        if (commentMapper.deleteByIdAndUserId(commentId, userId) == 0) {
            throw new IllegalStateException("评论删除失败");
        }

        int deletedCount = 1;
        if (comment.getParentId() == null) {
            deletedCount += commentMapper.deleteRepliesByParentId(commentId);
        } else if (commentMapper.updateReplyCount(comment.getParentId(), -1) == 0) {
            throw new IllegalStateException("回复数量更新失败");
        }

        if (socialMapper.updateCommentCount(comment.getPostId(), -deletedCount) == 0) {
            throw new IllegalStateException("评论数更新失败");
        }
        return deletedCount;
    }

    private Comment buildComment(Long userId, Long postId, String content, Integer isAnonymous) {
        String normalizedContent = normalizeContent(content);
        int anonymous = Integer.valueOf(1).equals(isAnonymous) ? 1 : 0;

        User user = userMapper.getByUserId(String.valueOf(userId));
        if (user == null) {
            throw new IllegalArgumentException("用户资料不存在");
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setUsername(anonymous == 1 ? "匿名用户" : defaultUsername(user, userId));
        comment.setAvatar(anonymous == 1 ? null : user.getAvatar());
        comment.setIsAnonymous(anonymous);
        comment.setContent(normalizedContent);
        comment.setStatus(1);
        comment.setLikeCount(0);
        comment.setReplyCount(0);
        return comment;
    }

    private Map<String, Object> pageResult(List<Comment> comments, int total, int page,
                                           int size, Long currentUserId) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (Comment comment : comments) {
            data.add(toView(comment, currentUserId));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    private Map<String, Object> toView(Comment comment, Long currentUserId) {
        boolean anonymous = Integer.valueOf(1).equals(comment.getIsAnonymous());
        Map<String, Object> item = new HashMap<>();
        item.put("id", comment.getId());
        item.put("postId", comment.getPostId());
        item.put("username", anonymous ? "匿名用户" : comment.getUsername());
        item.put("avatar", anonymous ? null : comment.getAvatar());
        item.put("isAnonymous", comment.getIsAnonymous());
        item.put("content", comment.getContent());
        item.put("parentId", comment.getParentId());
        item.put("replyToCommentId", comment.getReplyToCommentId());
        item.put("likeCount", comment.getLikeCount() == null ? 0 : comment.getLikeCount());
        item.put("replyCount", comment.getReplyCount() == null ? 0 : comment.getReplyCount());
        item.put("createdAt", comment.getCreatedAt());
        item.put("canDelete", currentUserId != null && currentUserId.equals(comment.getUserId()));
        return item;
    }

    private Post requireNormalPost(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("帖子 ID 不能为空");
        }
        Post post = socialMapper.getById(postId);
        if (post == null) {
            throw new IllegalArgumentException("帖子不存在");
        }
        return post;
    }

    private Comment requireNormalComment(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("评论 ID 不能为空");
        }
        Comment comment = commentMapper.getById(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("评论不存在");
        }
        return comment;
    }

    private void requireUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("请先登录");
        }
    }

    private String normalizeContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("评论内容不能为空");
        }
        String normalized = content.trim();
        if (normalized.length() > 300) {
            throw new IllegalArgumentException("评论内容不能超过 300 字");
        }
        return normalized;
    }

    private String defaultUsername(User user, Long userId) {
        String nickname = user.getNickname();
        return nickname == null || nickname.isBlank() ? "用户" + userId : nickname;
    }

    private int normalizePage(int page) {
        return Math.max(page, 1);
    }

    private int normalizeSize(int size) {
        return Math.min(Math.max(size, 1), 50);
    }
}
