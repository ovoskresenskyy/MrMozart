package com.example.mzrt.controller;

import com.example.mzrt.model.User;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "security/login";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", User.builder()
                .build());
        return "security/register";
    }

    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            result.rejectValue("email",
                    "isExist",
                    "There is already an account registered with the same email");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", User.builder().build());
            return "redirect:/register?error";
        }

        userService.saveUser(user, "ADMIN");
        return "redirect:/login";
    }

}
