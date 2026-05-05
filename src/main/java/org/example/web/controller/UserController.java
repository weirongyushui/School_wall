package org.example.web.controller;

import org.example.web.entity.User;
import org.example.web.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private UserProfileService userProfileService;

    /**
     * 从会话读取的值来自 login.user_id。
     * 库中外键为 login.user_id → user.user_id（学号），不是 user.id。
     * 兼容 Integer / Long / Number，避免强制转型 ClassCastException。
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
     * 与 Navicat 外键一致：login.user_id 引用 user.user_id（学号）。
     * 先按学号查；若无记录再按 user.id 查，兼容历史数据把 login.user_id 填成主键的情况。
     */
    private User resolveUserByLoginRef(Long ref) {
        if (ref == null) {
            return null;
        }
        String asStudentNo = String.valueOf(ref);
        User byStudentNo = userProfileService.getByUserId(asStudentNo);
        if (byStudentNo != null) {
            return byStudentNo;
        }
        return userProfileService.getById(ref);
    }

    // 页面映射
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    public String userPage() {
        return "User";
    }

    /**
     * 必须放在带路径变量的映射之前；且 /api/user/{id} 使用 \\d+，避免 "current" 被当成 id 导致 400。
     */
    @GetMapping("/api/user/current")
    @ResponseBody
    public User getCurrentUser(HttpSession session) {
        Long ref = sessionUserRef(session);
        return resolveUserByLoginRef(ref);
    }

    @GetMapping("/api/user/{id:\\d+}")
    @ResponseBody
    public User getById(@PathVariable("id") Long id, HttpSession session) {
        Long sessionRef = sessionUserRef(session);
        if (sessionRef == null || !sessionRef.equals(id)) {
            return null;
        }
        return resolveUserByLoginRef(sessionRef);
    }

    @PostMapping("/api/user/update")
    @ResponseBody
    public User updateUser(@RequestBody User user) {
        return userProfileService.saveUserProfile(user);
    }
}
