package org.example.web.controller;

import org.example.web.entity.User;
import org.example.web.service.LoginCheckService;
import org.example.web.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import jakarta.servlet.http.HttpSession;

/**
 * 用户中心 Controller
 * 负责用户信息相关接口与页面映射：
 * - /、/user 页面映射
 * - /api/user/current 当前登录用户信息
 * - /api/user/{id} 指定 ID 用户信息（仅能查自己）
 * - /api/user/update 更新用户资料
 * 用户 ID 标识使用 login.user_id（学号），与 user.user_id 对应；
 * 历史数据中可能存在 login.user_id 写成 user.id 主键的情况，已通过 resolveUserByLoginRef 兼容。
 */
@Controller
public class UserController {
    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private LoginCheckService loginCheckService;

    /**
     * 与 Navicat 外键一致：login.user_id 引用 user.user_id（学号）。
     * 先按学号查；若无记录再按 user.id 查，兼容历史数据把 login.user_id 填成主键的情况。
     */
    private User resolveUserByLoginRef(Long ref) {
        if (ref == null) {
            return null;
        }
        // 先把 ref 当学号查；查不到再按主键 id 查
        String asStudentNo = String.valueOf(ref);
        User byStudentNo = userProfileService.getByUserId(asStudentNo);
        if (byStudentNo != null) {
            return byStudentNo;
        }
        return userProfileService.getById(ref);
    }

    // ==================== 页面映射 ====================

    /**
     * 首页映射
     * @return 视图名 "index"
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 用户中心页面映射
     * @return 视图名 "User"
     */
    @GetMapping("/user")
    public String userPage() {
        return "User";
    }

    /**
     * 获取当前登录用户信息
     * 必须放在带路径变量的映射之前；且 /api/user/{id} 使用 \\d+，避免 "current" 被当成 id 导致 400。
     * @param session 当前会话
     * @return User 实体；未登录时返回 null
     */
    @GetMapping("/api/user/current")
    @ResponseBody
    public User getCurrentUser(HttpSession session) {
        Long ref = loginCheckService.check(session).userId();
        return resolveUserByLoginRef(ref);
    }


    /**
     * 更新用户资料接口
     * 服务端用 Session 中的当前用户覆盖请求体中的 id、userId，
     * 防止用户通过请求修改他人资料或篡改学号。
     * @param user    请求体 User 实体
     * @param session 当前会话
     * @return 更新后的 User；未登录或当前用户不存在时返回 null
     */
    @PostMapping("/api/user/update")
    @ResponseBody
    public User updateUser(@RequestBody User user,HttpSession session) {
        Long sessionRef = loginCheckService.check(session).userId();
        if (sessionRef==null){
            return null;
        }
        // 取当前登录用户，用于覆盖请求体中的身份字段
        User currentUser = resolveUserByLoginRef(sessionRef);
        if (currentUser==null){
            return null;
        }
        // 强制以会话身份为准，防止前端篡改主键
        user.setId(currentUser.getId());
        /*
         * 学号也不允许用户通过请求修改。
         */
        user.setUserId(currentUser.getUserId());

        return userProfileService.saveUserProfile(user);
    }
}
