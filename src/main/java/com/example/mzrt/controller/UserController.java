package com.example.mzrt.controller;

import com.example.mzrt.model.User;
import com.example.mzrt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users/list";
    }

    @GetMapping("/new")
    public String addNewForm(Model model) {
        model.addAttribute("user", User.builder().build());
        return "users/new";
    }

    @PostMapping
    public String save(@ModelAttribute("user") User user) {
        userService.saveUser(user, "USER");
        return "redirect:/users";
    }

    @GetMapping("/{id}/updating")
    public String updatingForm(@PathVariable(value = "id") int id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/update";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable(value = "id") int id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}
