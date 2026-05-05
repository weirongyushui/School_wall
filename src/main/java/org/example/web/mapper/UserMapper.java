package org.example.web.mapper;

import org.example.web.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getById(Long id);
    User getByUserId(String userId);
    User getByuserId(Integer id);
    int insert(User user);
    int update(User user);
    int deleteById(Long id);
}
