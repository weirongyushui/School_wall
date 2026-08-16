package org.example.web.controller;

import org.example.web.entity.Login;
import org.example.web.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录/注册/退出 Controller
 * 负责用户身份认证相关接口与页面映射：
 * - /login、/register 页面映射
 * - /api/login 登录接口
 * - /api/register 注册接口
 * - /api/logout 退出登录接口
 * 认证通过后使用 HttpSession 保存登录状态。
 */
@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 登录页视图映射
     * @return 视图名 "Login"，由 Thymeleaf 解析到 templates/Login.html
     */
    @GetMapping("/login")
    public String loginPage() {
        return "Login";
    }

    /**
     * 注册页视图映射
     * @return 视图名 "Register"，由 Thymeleaf 解析到 templates/Register.html
     */
    @GetMapping("/register")
    public String registerPage() {
        return "Register";
    }

    /**
     * 登录接口
     * 支持用户名或学号（9 位纯数字）登录；密码使用 MD5 校验，兼容历史明文数据。
     * @param request  请求体：username、password
     * @param session  当前会话，登录成功后写入 userId、username
     * @return success=true 时 message="登录成功"；false 时 message="用户名或密码错误"
     */
    @PostMapping("/api/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String, String> request,
                                      HttpSession session) {
        String username = request.get("username");
        String password = request.get("password");

        // 调用 Service 校验账号密码，返回 Login 实体（失败返回 null）
        Login login = loginService.login(username, password);

        Map<String, Object> result = new HashMap<>();
        if (login != null) {
            // 取 login.user_id（学号）作为会话标识，便于后续按学号查 user 表
            Long bindId = login.getUserId();
            session.setAttribute("userId", bindId);
            session.setAttribute("username", login.getUsername());
            session.setAttribute("role", login.getRole());

            result.put("success", true);
            result.put("message", "登录成功");
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

    /**
     * 注册接口
     * 同时创建 user 表记录（个人资料）和 login 表记录（账号密码）。
     * @param request  请求体：studentId、username、password、nickname、email
     * @return success=true 时 message="注册成功"；false 时 message=具体错误信息
     */
    @PostMapping("/api/register")
    @ResponseBody
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        String studentId = request.get("studentId");
        String username = request.get("username");
        String password = request.get("password");
        String nickname = request.get("nickname");
        String email = request.get("email");

        // Service 内会做学号格式、用户名/学号重复等校验，返回错误消息或 null
        String error = loginService.register(studentId, username, password, nickname, email);

        Map<String, Object> result = new HashMap<>();
        if (error == null) {
            result.put("success", true);
            result.put("message", "注册成功");
        } else {
            result.put("success", false);
            result.put("message", error);
        }
        return result;
    }

    /**
     * 退出登录接口
     * 仅清除 Session 中的 userId、username，不销毁整个 Session。
     * @param session 当前会话
     * @return success=true，message="退出成功"
     */
    @PostMapping("/api/logout")
    @ResponseBody
    public Map<String, Object> logout(HttpSession session) {
        session.removeAttribute("userId");
        session.removeAttribute("username");

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "退出成功");
        return result;
    }
}
