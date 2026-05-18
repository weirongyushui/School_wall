package org.example.web.controller;

import org.example.web.entity.Post;
import org.example.web.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态广场 Controller
 */
@Controller
public class SocialController {

    @Autowired
    private PostService postService;

    private static Long sessionUserRef(HttpSession session) {
        Object v = session.getAttribute("userId");
        if (v == null) {
            return null;
        }
        if (v instanceof Long) {
            return (Long) v;
        }
        if (v instanceof Integer) {
            return ((Integer) v).longValue();
        }
        if (v instanceof Number) {
            return ((Number) v).longValue();
        }
        if (v instanceof String) {
            try {
                return Long.parseLong(((String) v).trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 访问 /social 页面
     */
    @GetMapping("/social")
    public String socialPage() {
        return "Social";
    }

    /**
     * 发布帖子
     */
    @PostMapping("/api/post/create")
    @ResponseBody
    public Map<String, Object> createPost(@RequestBody Map<String, String> request,
                                           HttpSession session) {
        Long userId = sessionUserRef(session);

        String content = request.get("content");
        String categoryStr = request.get("category");
        String isAnonymousStr = request.get("isAnonymous");

        Integer category = null;
        if (categoryStr != null && !categoryStr.isEmpty()) {
            try {
                category = Integer.parseInt(categoryStr);
            } catch (NumberFormatException e) {
                category = null;
            }
        }

        Integer isAnonymous = null;
        if (isAnonymousStr != null && !isAnonymousStr.isEmpty()) {
            try {
                isAnonymous = Integer.parseInt(isAnonymousStr);
            } catch (NumberFormatException e) {
                isAnonymous = null;
            }
        }

        String error = postService.createPost(userId, content, category, isAnonymous);

        Map<String, Object> result = new HashMap<>();
        if (error == null) {
            result.put("success", true);
            result.put("message", "发帖成功");
        } else {
            result.put("success", false);
            result.put("message", error);
        }
        return result;
    }

    /**
     * 获取帖子列表
     */
    @GetMapping("/api/post/list")
    @ResponseBody
    public Map<String, Object> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) Integer category) {

        Map<String, Object> serviceResult = postService.getPostList(page, size, category);
        List<Post> list = (List<Post>) serviceResult.get("list");
        int total = (int) serviceResult.get("total");

        Map<String, Object> response = new HashMap<>();
        response.put("data", list != null ? list : new ArrayList<>());
        response.put("total", total);
        return response;
    }
}
