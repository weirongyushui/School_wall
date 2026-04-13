package org.example.web.controller;

import org.example.web.entity.User;
import org.example.web.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:63342", "http://127.0.0.1:63342"})
public class UserController {
    @Autowired
    private UserProfileService userProfileService;
    
    @GetMapping("/{id}")
    public User getById(@PathVariable("id") Long id){
        return userProfileService.getById(id);
    }
    
    @PostMapping("/update")
    public User updateUser(@RequestBody User user){
        return userProfileService.saveUserProfile(user);
    }
}

