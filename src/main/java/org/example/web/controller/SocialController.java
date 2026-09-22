package org.example.web.controller;

import org.example.web.entity.Post;
import org.example.web.dto.LoginCheckResult;
import org.example.web.service.LoginCheckService;
import org.example.web.service.PostLikeService;
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

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private LoginCheckService loginCheckService;

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
        Long userId = loginCheckService.check(session).userId();

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
        Long currentUserId = loginCheckService.check(session).userId();
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
                item.put("liked",
                        currentUserId != null
                                && postLikeService.hasLiked(currentUserId, post.getId()));

                boolean canDelete =
                        currentUserId != null
                                && currentUserId.equals(post.getUserId());

                item.put("canDelete", canDelete);

                safeList.add(item);
            }
        }

        // 统一对外返回 data/total 字段，list 为空时返回空数组而非 null
        Map<String, Object> response = new HashMap<>();
        response.put("data", safeList);
        response.put("total", total);
        return response;
    }

    /**
     * 点赞帖子。
     */
    @PostMapping("/api/post/{id}/like")
    @ResponseBody
    public Map<String, Object> likePost(@PathVariable Long id, HttpSession session) {
        LoginCheckResult login = loginCheckService.check(session);
        if (!login.loggedIn()) {
            return Map.of(
                    "success", false,
                    "liked", false,
                    "message", login.message()
            );
        }
        Long userId = login.userId();

        try {
            boolean changed = postLikeService.like(userId, id);
            boolean liked = postLikeService.hasLiked(userId, id);
            int likeCount = postLikeService.getLikeCount(id);

            return Map.of(
                    "success", true,
                    "liked", liked,
                    "changed", changed,
                    "likeCount", likeCount,
                    "message", changed ? "点赞成功" : "已经点过赞"
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "liked", false,
                    "message", e.getMessage()
            );
        } catch (RuntimeException e) {
            return Map.of(
                    "success", false,
                    "liked", false,
                    "message", "点赞失败，请稍后重试"
            );
        }
    }

    /**
     * 取消点赞。
     */
    @DeleteMapping("/api/post/{id}/like")
    @ResponseBody
    public Map<String, Object> unlikePost(@PathVariable Long id, HttpSession session) {
        LoginCheckResult login = loginCheckService.check(session);
        if (!login.loggedIn()) {
            return Map.of(
                    "success", false,
                    "liked", false,
                    "message", login.message()
            );
        }
        Long userId = login.userId();

        try {
            boolean changed = postLikeService.unlike(userId, id);
            boolean liked = postLikeService.hasLiked(userId, id);
            int likeCount = postLikeService.getLikeCount(id);

            return Map.of(
                    "success", true,
                    "liked", liked,
                    "changed", changed,
                    "likeCount", likeCount,
                    "message", changed ? "取消点赞成功" : "尚未点赞"
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "liked", false,
                    "message", e.getMessage()
            );
        } catch (RuntimeException e) {
            return Map.of(
                    "success", false,
                    "liked", false,
                    "message", "取消点赞失败，请稍后重试"
            );
        }
    }

    /**
     * 查询当前用户是否已点赞指定帖子。
     */
    @GetMapping("/api/post/{id}/like/status")
    @ResponseBody
    public Map<String, Object> getPostLikeStatus(@PathVariable Long id, HttpSession session) {
        Long userId = loginCheckService.check(session).userId();
        try {
            return Map.of(
                    "success", true,
                    "liked", postLikeService.hasLiked(userId, id),
                    "likeCount", postLikeService.getLikeCount(id)
            );
        } catch (IllegalArgumentException e) {
            return Map.of(
                    "success", false,
                    "liked", false,
                    "message", e.getMessage()
            );
        }
    }

    /**
     * TODO(P1-帖子详情): GET /api/post/{id}。
     * 先查询 status=正常 的帖子，再按与列表相同的规则构造安全 DTO；附带 canDelete、liked，
     * 评论通过独立分页接口加载。找不到帖子时返回 404，不要把数据库实体（尤其匿名 userId）直接返回。
     */

    /**
     * TODO(P1-我的帖子): GET /api/post/mine?page=1&size=20。
     * userId 只能从 Session 获取，Service 的分页查询增加 userId 条件；返回列表仍使用统一安全 DTO，
     * 个人中心只负责调用接口和渲染，避免复制一套帖子权限判断。
     */

    /**
     * TODO(P1-图片上传): POST /api/post/images，使用 MultipartFile 接收，可支持多文件。
     * 逐个校验文件非空、真实 MIME/扩展名、单张大小和总数量；生成随机文件名存到固定目录，
     * 仅返回可访问 URL。若批量中途失败，要清理本次已保存文件；发帖时再保存这些 URL。
     */

    /**
     * TODO(P1-举报审核): 建议独立 ReportController/Service/Mapper，并建立 report 与 review_record 表。
     * 举报接口记录 target_type、target_id、reason、reporter_id，并限制同一用户重复举报；
     * 待审核/通过/屏蔽接口必须经过 ReviewAuthInterceptor。审核时在同一事务中更新举报状态、
     * 目标内容状态和审核记录，保留 reviewer_id、处理意见、处理时间，便于追溯。
     */

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
        Long userId = loginCheckService.check(session).userId();
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
