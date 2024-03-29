package com.example.mzrt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "security/login";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/users";
    }
}
