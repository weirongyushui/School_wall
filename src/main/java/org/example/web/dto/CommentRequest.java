package org.example.web.dto;

/**
 * 发表评论或回复时的请求体。
 */
public record CommentRequest(String content, Integer isAnonymous) {
}
