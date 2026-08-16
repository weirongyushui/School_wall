package org.example.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ReviewAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);

        // 1. 没有 Session，说明还没有登录
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"请先登录\"}"
            );
            return false;
        }

        // 2. 登录身份和角色都由服务器 Session 提供
        Object userId = session.getAttribute("userId");
        Object roleValue = session.getAttribute("role");

        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"请先登录\"}"
            );
            return false;
        }

        String role = roleValue instanceof String
                ? (String) roleValue
                : null;

        // 3. 只有审核员和管理员可以进入审核接口
        boolean canReview =
                "REVIEWER".equals(role)
                        || "ADMIN".equals(role);

        if (!canReview) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"success\":false,\"message\":\"无审核权限\"}"
            );
            return false;
        }

        // 4. 身份合法，允许请求继续进入 Controller
        return true;
    }
}