package com.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/api/auth/register")
    public String registerRedirect() {
        return "redirect:/register";
    }

    @GetMapping("/api/auth/login")
    public String loginRedirect() {
        return "redirect:/login";
    }
} 