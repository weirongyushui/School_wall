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
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private SocialMapper socialMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public String createPost(Long userId, String content, Integer category, Integer isAnonymous) {
        if (userId == null) {
            return "请先登录";
        }
        if (content == null || content.trim().isEmpty()) {
            //.trim()去除字符串首尾的空白字符（包括空格、制表符、换行等）。它会返回一个新字符串，原字符串内容不变。
            return "内容不能为空";
        }
        content = content.trim();
        if (content.length() > 500) {
            return "内容不能超过 500 字";
        }
        if (category == null || category < 1 || category > 4) {
            return "请选择分区";
        }
        if (isAnonymous == null) {
            isAnonymous = 0;//是否匿名
        }

        // 查 user 表获取昵称和头像，设定初始为空
        String nickname = null;
        String avatar = null;

        if (isAnonymous == 0) {
            // 通过 user.user_id（学号）查用户信息
            User user = userMapper.getByUserId(String.valueOf(userId));
            if (user != null) {
                nickname = user.getNickname();
                avatar = user.getAvatar();
            }
            if (nickname == null || nickname.isEmpty()) {
                nickname = "用户" + userId;
            }
        } else {
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

        socialMapper.insert(post);

        return null;
    }

    @Override
    public Map<String, Object> getPostList(int page, int size, Integer category) {
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
}
