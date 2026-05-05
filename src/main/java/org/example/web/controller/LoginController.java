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

@Controller
public class LoginController {
   
    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public String loginPage() {
        return "Login";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Map<String, Object> login(@RequestBody Map<String, String> request,
                                      HttpSession session) {
        String username = request.get("username");
        String password = request.get("password");
        
        Login login = loginService.login(username, password);
        
        Map<String, Object> result = new HashMap<>();
        if (login != null) {
            // 统一存 Long，避免部分容器/序列化写成 Integer 导致取会话时 ClassCastException
            Long bindId = login.getUserId();
            session.setAttribute("userId", bindId);
            session.setAttribute("username", login.getUsername());
            
            result.put("success", true);
            result.put("message", "登录成功");
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }

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
