package org.example.web.service;

import org.example.web.entity.User;
import org.example.web.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserProfileService {
    @Autowired
    private UserMapper userMapper;

    public User getById(Long id){
        return userMapper.getById(id);
    }

    public User saveUserProfile(User user){
        Date now = new Date();
        user.setUpdatedAt(now);

        if (user.getId() > 0) {
            User existed = userMapper.getById(user.getId());
            if (existed != null) {
                userMapper.update(user);
                return userMapper.getById(user.getId());
            }
        }

        user.setId(0);
        user.setCreatedAt(now);
        userMapper.insert(user);
        return userMapper.getById(user.getId());
    }

    public int insert(User user){
        return userMapper.insert(user);
    }

    public int update(User user){
        return userMapper.update(user);
    }

    public int deleteById(Long id){
        return userMapper.deleteById(id);
    }
}
