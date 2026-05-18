package org.example.web.service;

import org.example.web.entity.Login;
import org.example.web.entity.User;
import org.example.web.mapper.LoginMapper;
import org.example.web.mapper.UserMapper;
import org.example.web.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class LoginService {
    @Autowired
    private LoginMapper loginMapper;

    @Autowired
    private UserMapper userMapper;

    public Login login(String username, String password) {
        if (username == null) {
            return null;
        }
        String account = username.trim();
        Login login = loginMapper.getByUsername(account);
        // 与 login.user_id（外键 user.user_id，学号）一致时，允许用学号登录
        if (login == null && account.matches("\\d+")) {
            try {
                login = loginMapper.getByUserId(Long.parseLong(account));
            } catch (NumberFormatException ignored) {
                login = null;
            }
        }
        if (login == null) {
            return null;
        }
        String stored = login.getPassword();
        if (stored == null) {
            return null;
        }
        // 与 createLogin / changePassword 一致：库中为 MD5；旧数据可能仍为明文
        if (MD5Util.verify(password, stored) || password.equals(stored)) {
            return login;
        }
        return null;
    }

    public Login getLoginByUsername(String username) {
        return loginMapper.getByUsername(username);
    }

    public Login getLoginByUserId(Long userId) {
        return loginMapper.getByUserId(userId);
    }

    public Login createLogin(Login login) {
        Login existed = loginMapper.getByUsername(login.getUsername());
        if (existed != null) {
            return null;
        }
        login.setPassword(MD5Util.md5(login.getPassword()));
        Date now = new Date();
        login.setCreatedAt(now);
        login.setUpdatedAt(now);
        loginMapper.insert(login);
        return login;
    }

    public Login updateLogin(Login login) {
        Date now = new Date();
        login.setUpdatedAt(now);
        loginMapper.update(login);
        return loginMapper.getById(login.getId());
    }

    public int deleteLogin(Long id) {
        return loginMapper.deleteById(id);
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Login login = loginMapper.getByUserId(userId);
        if (login == null) {
            return false;
        }
        if (!MD5Util.verify(oldPassword, login.getPassword())) {
            return false;
        }
        login.setPassword(MD5Util.md5(newPassword));
        Date now = new Date();
        login.setUpdatedAt(now);
        loginMapper.update(login);
        return true;
    }

    /**
     * 用户注册：同时创建 login 记录和 user 记录
     * @param studentId 学号（9 位数字字符串，如 "244010215"）
     * @param username  用户名
     * @param password  明文密码（内部 MD5 加密）
     * @param nickname  昵称
     * @param email     邮箱
     * @return 错误消息或 null（null 表示成功）
     */
    @Transactional
    public String register(String studentId, String username, String password, String nickname, String email) {
        if (username == null || username.trim().isEmpty()) {
            return "用户名不能为空";
        }
        if (studentId == null || studentId.trim().isEmpty()) {
            return "学号不能为空";
        }
        if (!studentId.trim().matches("\\d+")) {
            return "学号必须为纯数字";
        }
        if (studentId.trim().length() != 9) {
            return "学号必须为 9 位数字";
        }
        if (password == null || password.length() < 6) {
            return "密码长度至少 6 位";
        }

        username = username.trim();
        studentId = studentId.trim();

        long studentIdLong;
        try {
            studentIdLong = Long.parseLong(studentId);
        } catch (NumberFormatException e) {
            return "学号格式错误";
        }

        // 检查用户名是否已存在
        Login existByUsername = loginMapper.getByUsername(username);
        if (existByUsername != null) {
            return "用户名已被注册";
        }

        // 检查学号是否已注册（login 表）
        Login existByUserId = loginMapper.getByUserId(studentIdLong);
        if (existByUserId != null) {
            return "该学号已被注册";
        }

        // 检查学号是否已注册（user 表）
        User existUser = userMapper.getByUserId(studentId);
        if (existUser != null) {
            return "该学号已被注册";
        }

        Date now = new Date();

        // 1. 创建 user 记录
        User user = new User();
        user.setUserId(studentId);
        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname.trim());
        } else {
            user.setNickname(username);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email.trim());
        }
        user.setGender(-1);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userMapper.insert(user);

        // 2. 创建 login 记录
        Login login = new Login();
        login.setUsername(username);
        login.setPassword(MD5Util.md5(password));
        login.setUserId(studentIdLong);
        login.setCreatedAt(now);
        login.setUpdatedAt(now);
        loginMapper.insert(login);

        return null;
    }
}
