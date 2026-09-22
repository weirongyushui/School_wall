package org.example.web.dto;

/**
 * Session 登录检查结果。
 */
public record LoginCheckResult(boolean loggedIn, Long userId, String message) {

    public static LoginCheckResult loggedIn(Long userId) {
        return new LoginCheckResult(true, userId, null);
    }

    public static LoginCheckResult notLoggedIn() {
        return new LoginCheckResult(false, null, "未登录");
    }
}
