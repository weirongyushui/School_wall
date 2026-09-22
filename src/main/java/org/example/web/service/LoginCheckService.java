package org.example.web.service;

import jakarta.servlet.http.HttpSession;
import org.example.web.dto.LoginCheckResult;
import org.springframework.stereotype.Service;

/**
 * 统一检查 Session 登录状态并解析当前用户 ID。
 */
@Service
public class LoginCheckService {

    public LoginCheckResult check(HttpSession session) {
        if (session == null) {
            return LoginCheckResult.notLoggedIn();
        }

        Long userId = parseUserId(session.getAttribute("userId"));
        return userId == null
                ? LoginCheckResult.notLoggedIn()
                : LoginCheckResult.loggedIn(userId);
    }

    private Long parseUserId(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }
}
