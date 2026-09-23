package org.example.web.service;

import java.util.Map;

public interface CommentService {

    Map<String, Object> getTopLevelComments(Long currentUserId, Long postId, int page, int size);

    Map<String, Object> getReplies(Long currentUserId, Long parentId, int page, int size);

    Map<String, Object> createComment(Long userId, Long postId, String content, Integer isAnonymous);

    Map<String, Object> createReply(Long userId, Long targetCommentId, String content, Integer isAnonymous);

    int deleteComment(Long userId, Long commentId);
}
