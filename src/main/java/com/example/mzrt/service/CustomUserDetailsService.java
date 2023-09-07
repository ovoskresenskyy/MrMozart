package com.example.mzrt.service;

import com.example.mzrt.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public CustomUserDetailsService(UserService userService,
                                    RoleService roleService) {

        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> existingUser = userService.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getPassword(),
                    roleService.findAll());
        } else throw new UsernameNotFoundException("Invalid username or password.");
    }
}
