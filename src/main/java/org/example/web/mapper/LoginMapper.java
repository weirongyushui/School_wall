package org.example.web.mapper;

import org.example.web.entity.Login;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginMapper {
    Login getById(Long id);
    Login getByUsername(String username);
    Login getByUserId(Long userId);
    int insert(Login login);
    int update(Login login);
    int deleteById(Long id);
}
