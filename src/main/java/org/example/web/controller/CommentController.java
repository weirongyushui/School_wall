package org.example.web.controller;

import jakarta.servlet.http.HttpSession;
import org.example.web.dto.CommentRequest;
import org.example.web.dto.LoginCheckResult;
import org.example.web.service.CommentService;
import org.example.web.service.LoginCheckService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final LoginCheckService loginCheckService;

    public CommentController(CommentService commentService, LoginCheckService loginCheckService) {
        this.commentService = commentService;
        this.loginCheckService = loginCheckService;
    }

    @GetMapping("/post/{postId}/comments")
    public Map<String, Object> getComments(@PathVariable Long postId,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "20") int size,
                                           HttpSession session) {
        try {
            Map<String, Object> result = new HashMap<>(commentService.getTopLevelComments(
                    loginCheckService.check(session).userId(), postId, page, size));
            result.put("success", true);
            return result;
        } catch (IllegalArgumentException e) {
            return failure(e.getMessage());
        } catch (RuntimeException e) {
            return failure("评论加载失败，请稍后重试");
        }
    }

    @GetMapping("/comments/{commentId}/replies")
    public Map<String, Object> getReplies(@PathVariable Long commentId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "20") int size,
                                          HttpSession session) {
        try {
            Map<String, Object> result = new HashMap<>(commentService.getReplies(
                    loginCheckService.check(session).userId(), commentId, page, size));
            result.put("success", true);
            return result;
        } catch (IllegalArgumentException e) {
            return failure(e.getMessage());
        } catch (RuntimeException e) {
            return failure("回复加载失败，请稍后重试");
        }
    }

    @PostMapping("/post/{postId}/comments")
    public Map<String, Object> createComment(@PathVariable Long postId,
                                             @RequestBody CommentRequest request,
                                             HttpSession session) {
        LoginCheckResult login = loginCheckService.check(session);
        if (!login.loggedIn()) {
            return failure(login.message());
        }
        try {
            return success("评论成功", commentService.createComment(
                    login.userId(), postId, request.content(), request.isAnonymous()));
        } catch (IllegalArgumentException e) {
            return failure(e.getMessage());
        } catch (RuntimeException e) {
            return failure("评论失败，请稍后重试");
        }
    }

    @PostMapping("/comments/{commentId}/replies")
    public Map<String, Object> createReply(@PathVariable Long commentId,
                                           @RequestBody CommentRequest request,
                                           HttpSession session) {
        LoginCheckResult login = loginCheckService.check(session);
        if (!login.loggedIn()) {
            return failure(login.message());
        }
        try {
            return success("回复成功", commentService.createReply(
                    login.userId(), commentId, request.content(), request.isAnonymous()));
        } catch (IllegalArgumentException e) {
            return failure(e.getMessage());
        } catch (RuntimeException e) {
            return failure("回复失败，请稍后重试");
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public Map<String, Object> deleteComment(@PathVariable Long commentId, HttpSession session) {
        LoginCheckResult login = loginCheckService.check(session);
        if (!login.loggedIn()) {
            return failure(login.message());
        }
        try {
            int deletedCount = commentService.deleteComment(login.userId(), commentId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "删除成功");
            result.put("deletedCount", deletedCount);
            return result;
        } catch (IllegalArgumentException e) {
            return failure(e.getMessage());
        } catch (RuntimeException e) {
            return failure("删除失败，请稍后重试");
        }
    }

    private Map<String, Object> success(String message, Map<String, Object> comment) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("comment", comment);
        return result;
    }

    private Map<String, Object> failure(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message == null ? "操作失败" : message);
        return result;
    }
}
