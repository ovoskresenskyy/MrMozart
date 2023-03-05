package com.example.mzrt.controller;

import com.example.mzrt.model.User;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/new")
    public String userNewForm(Model model) {
        model.addAttribute("user", User.builder().build());
        return "users/new";
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @PostMapping("/users")
    public String saveAlert(@ModelAttribute("user") User user) {
        userService.saveUser(user, "USER");
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/updating")
    public String updatingForm(@PathVariable(value = "id") int id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/update";
    }

    @DeleteMapping("/users/{id}")
    public String delete(@PathVariable(value = "id") int id) {
        userService.deleteById(id);
        return "redirect:/users";
    }

}
