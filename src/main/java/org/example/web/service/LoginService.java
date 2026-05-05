package org.example.web.service;

import org.example.web.entity.Login;
import org.example.web.mapper.LoginMapper;
import org.example.web.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginService {
    @Autowired
    private LoginMapper loginMapper;

    public Login login(String username, String password) {
        Login login = loginMapper.getByUsername(username);
        if (login == null) {
            return null;
        }
        if (MD5Util.verify(password, login.getPassword())) {
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
}
