package com.example.mzrt.service;

import com.example.mzrt.model.Role;
import com.example.mzrt.model.User;
import com.example.mzrt.repository.RoleRepository;
import com.example.mzrt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User incomingData, String role) {

        User savedUser = userRepository.save(User.builder()
                .name(incomingData.getName())
                .email(incomingData.getEmail())
                .password(passwordEncoder.encode(incomingData.getPassword()))
                .build());

        addRole(savedUser.getId(), role);

        return savedUser;
    }

    private void addRole(int userId, String role) {
        roleRepository.save(Role.builder()
                .role(role)
                .userId(userId)
                .build());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}