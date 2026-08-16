package org.example.web.controller;

import org.example.web.entity.Post;
import org.example.web.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * 负责帖子相关接口与动态广场页面映射：
 * - /social 页面映射
 * - /api/post/create 发布帖子
 * - /api/post/list 帖子列表（分页 + 分类筛选）
 * - /api/post/{id} 删除帖子（DELETE，软删 status=0）
 * 当前未实现的接口（前端已调用，会 404）：
 * - /api/post/{id}/like 点赞
 * - /api/post/hot 热门帖子
 * - PUT /api/post/{id} 更新帖子
 */
@Controller
public class SocialController {

    @Autowired
    private PostService postService;

    /**
     * 从 Session 读取当前登录用户 ID（login.user_id，即学号）。
     * 兼容 Long / Integer / Number / String 多种类型，避免 ClassCastException。
     *
     * @param session 当前会话
     * @return 学号 Long；未登录或类型异常时返回 null
     */
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
     *
     * @return 视图名 "Social"，由 Thymeleaf 解析到 templates/Social.html
     */
    @GetMapping("/social")
    public String socialPage() {
        return "Social";
    }

    /**
     * 发布帖子接口
     *
     * @param request 请求体：content（正文）、category（分类 1~4）、isAnonymous（0/1）
     * @param session 当前会话，用于取登录用户 ID
     * @return success=true 时 message="发帖成功"；false 时 message=具体错误
     */
    @PostMapping("/api/post/create")
    @ResponseBody
    public Map<String, Object> createPost(@RequestBody Map<String, String> request,
                                          HttpSession session) {
        // 取登录用户 ID（学号），未登录时为 null，由 Service 返回"请先登录"
        Long userId = sessionUserRef(session);

        String content = request.get("content");
        String categoryStr = request.get("category");
        String isAnonymousStr = request.get("isAnonymous");

        // 前端传字符串，这里转为 Integer；空或非数字时保持 null，交给 Service 校验
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

        // 调用 Service 处理发帖逻辑，返回错误消息或 null
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
     * 获取帖子列表接口（分页 + 可选分类筛选）
     *
     * @param page     页码，默认 1
     * @param size     每页数量，默认 30
     * @param category 分类筛选，null 或 0 表示全部
     * @return data=List<Post>，total=总数
     */
    @GetMapping("/api/post/list")
    @ResponseBody
    public Map<String, Object> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) Integer category,HttpSession session) {

        // Service 返回 list/total 两个键
        Map<String, Object> serviceResult = postService.getPostList(page, size, category);
        List<Post> list = (List<Post>) serviceResult.get("list");
        int total = (int) serviceResult.get("total");
        Long currentUserId = sessionUserRef(session);
        List<Map<String, Object>> safeList = new ArrayList<>();
        if (list != null) {
            for (Post post : list) {
                Map<String, Object> item = new HashMap<>();

                item.put("id", post.getId());
                item.put("content", post.getContent());
                item.put("images", post.getImages());
                item.put("username",
                        Integer.valueOf(1).equals(post.getIsAnonymous())
                                ? "匿名用户"
                                : post.getUsername());
                item.put("avatar",
                        Integer.valueOf(1).equals(post.getIsAnonymous())
                                ? null
                                : post.getAvatar());
                item.put("isAnonymous", post.getIsAnonymous());
                item.put("category", post.getCategory());
                item.put("likeCount", post.getLikeCount());
                item.put("commentCount", post.getCommentCount());
                item.put("createdAt", post.getCreatedAt());

                boolean canDelete =
                        currentUserId != null
                                && currentUserId.equals(post.getUserId());

                item.put("canDelete", canDelete);

                safeList.add(item);
            }
        }

        // 统一对外返回 data/total 字段，list 为空时返回空数组而非 null
        Map<String, Object> response = new HashMap<>();
        response.put("data", list != null ? list : new ArrayList<>());
        response.put("total", total);
        return response;
    }

    /**
     * TODO: 点赞功能 - 前端已调用 /api/post/{id}/like
     * 当前未实现，前端调用会返回 404。
     */
    // @PostMapping("/api/post/{id}/like")
    // @ResponseBody
    // public Map<String, Object> likePost(@PathVariable Long id, HttpSession session) {
    //     // 待实现：验证登录、更新点赞数、返回结果
    // }

    /**
     * TODO: 获取热门帖子 - 前端已调用 /api/post/hot
     * 当前未实现，前端调用会返回 404。
     */
    // @GetMapping("/api/post/hot")
    // @ResponseBody
    // public Map<String, Object> getHotPosts() {
    //     // 待实现：按点赞数排序返回热门帖子
    // }

    /**
     * 删除帖子接口（软删除）
     * Service 内校验：必须登录且为帖子作者本人才能删除。
     *
     * @param id      帖子 ID
     * @param session 当前会话
     * @return success=true 时 message="删除成功"；false 时 message=具体错误
     */
    @DeleteMapping("/api/post/{id}")
    @ResponseBody
    public Map<String, Object> deletePost(@PathVariable Long id, HttpSession session) {
        Long userId = sessionUserRef(session);
        // Service 会校验登录、查帖、比对作者，并软删（status=0）
        String error = postService.deletePost(userId, id);

        Map<String, Object> result = new HashMap<>();
        if (error == null) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", error);
        }
        return result;
    }

    /**
     * TODO: 更新帖子
     * 当前未实现，Mapper 的 update SQL 已就绪但无业务代码调用。
     */
    // @PutMapping("/api/post/{id}")
    // @ResponseBody
    // public Map<String, Object> updatePost(@PathVariable Long id, @RequestBody Map<String, String> request, HttpSession session) {
    //     // 待实现：验证权限、更新帖子内容
    // }
}
