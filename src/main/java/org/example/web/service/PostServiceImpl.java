package org.example.web.service;

import org.example.web.entity.Post;
import org.example.web.entity.User;
import org.example.web.mapper.SocialMapper;
import org.example.web.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子服务实现
 * 实现 PostService 接口，提供发帖、列表、删除等业务逻辑。
 * 依赖 SocialMapper（帖子表）与 UserMapper（用于实名帖查昵称/头像）。
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private SocialMapper socialMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发布帖子
     * 校验：登录、内容 1~500 字、category 1~4；匿名帖固定 username="匿名用户"。
     * 插入 SQL 固定 status=1（直接发布），like_count=0，comment_count=0。
     */
    @Override
    public String createPost(Long userId, String content, Integer category, Integer isAnonymous) {
        // 1. 登录校验
        if (userId == null) {
            return "请先登录";
        }
        // 2. 内容校验
        if (content == null || content.trim().isEmpty()) {
            //.trim()去除字符串首尾的空白字符（包括空格、制表符、换行等）。它会返回一个新字符串，原字符串内容不变。
            return "内容不能为空";
        }
        content = content.trim();
        if (content.length() > 500) {
            return "内容不能超过 500 字";
        }
        // 3. 分类校验：1=发发牢骚 2=吹吹牛皮 3=校园互助 4=联名请愿
        if (category == null || category < 1 || category > 4) {
            return "请选择分区";
        }
        // 4. 匿名标志：null 默认 0（实名）
        if (isAnonymous == null) {
            isAnonymous = 0;//是否匿名
        }

        // 查 user 表获取昵称和头像，设定初始为空
        String nickname = null;
        String avatar = null;

        if (isAnonymous == 0) {
            // 实名帖：通过 user.user_id（学号）查用户信息，取出昵称和头像冗余写入帖子
            User user = userMapper.getByUserId(String.valueOf(userId));
            if (user != null) {
                nickname = user.getNickname();
                avatar = user.getAvatar();
            }
            // 查不到 user 记录时给默认昵称，避免帖子展示空用户名
            if (nickname == null || nickname.isEmpty()) {
                nickname = "用户" + userId;
            }
        } else {
            // 匿名帖：固定昵称，不写头像
            nickname = "匿名用户";
        }

        Date now = new Date();
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setCategory(category);
        post.setIsAnonymous(isAnonymous);
        post.setUsername(nickname);
        post.setAvatar(avatar);
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        // 插入 post 表，status 由 SQL 固定为 1（正常）
        socialMapper.insert(post);

        return null;
    }

    /**
     * 查询帖子列表（分页 + 可选分类筛选）
     * 仅返回 status=1 的帖子，按 created_at 倒序。
     */
    @Override
    public Map<String, Object> getPostList(int page, int size, Integer category) {
        // 组装 MyBatis 参数：offset/limit 必填，category 可选
        Map<String, Object> params = new HashMap<>();
        params.put("offset", (page - 1) * size);
        params.put("limit", size);
        if (category != null && category > 0) {
            params.put("category", category);
        }

        List<Post> list = socialMapper.getList(params);
        int total = socialMapper.getCount(params);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    /**
     * 删除帖子（软删除）
     * 校验：登录、帖子存在、作者本人才能删除。
     * 执行 update 把 status 改为 0，不物理删除。
     */
    @Override
    public String deletePost(Long userId, Long postId) {
        if (userId == null) {
            return "请先登录";
        }
        if (postId == null) {
            return "帖子 ID 不能为空";
        }

        // getById 仅查 status=1 的帖子，已删除的帖子查不到
        Post post = socialMapper.getById(postId);
        if (post == null) {
            return "帖子不存在";
        }

        // 权限校验：仅作者本人可删（比对 login.user_id 与 post.user_id）
        if (!userId.equals(post.getUserId())) {
            return "无权限删除此帖子";
        }

        // 软删除：status 改为 0
        socialMapper.deleteById(postId);
        return null;
    }

}
