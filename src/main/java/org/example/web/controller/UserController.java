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
    
    // 页面映射
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/user")
    public String userPage() {
        return "User";
    }

    // API 方法
    @GetMapping("/api/user/{id}")
    @ResponseBody
    public User getById(@PathVariable("id") Long id, HttpSession session){
        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null) {
            return null;
        }
        return userProfileService.getById(sessionUserId);
    }
    
    @GetMapping("/api/user/current")
    @ResponseBody
    public User getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }
        return userProfileService.getByUserId(String.valueOf(userId));
    }
    
    @PostMapping("/api/user/update")
    @ResponseBody
    public User updateUser(@RequestBody User user){
        return userProfileService.saveUserProfile(user);
    }
}

