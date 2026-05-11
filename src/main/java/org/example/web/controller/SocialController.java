package org.example.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 动态广场 Controller
 */
@Controller
public class SocialController {

    /**
     * 访问 /social 页面
     */
    @GetMapping("/social")
    public String socialPage() {
        return "Social";
    }
}
