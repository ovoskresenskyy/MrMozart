package com.example.mzrt.service;

import com.example.mzrt.model.Role;
import com.example.mzrt.model.User;
import com.example.mzrt.repository.RoleRepository;
import com.example.mzrt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

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

        String encodedPassword = passwordEncoder.encode(incomingData.getPassword());
        incomingData.setPassword(encodedPassword);

        User savedUser = userRepository.save(incomingData);
        addRole(savedUser.getId(), role);

        return savedUser;
    }

    public Optional<User> findByToken(String token) {
        return userRepository.findByToken(token);
    }

    private void addRole(int userId, String role) {

        Optional<Role> existedRole = roleRepository.findByUserIdAndRole(userId, role);
        if (existedRole.isEmpty()) roleRepository.save(Role.builder()
                .role(role)
                .userId(userId)
                .build());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public User findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    public void deleteById(int id) {
        userRepository.deleteById(id);
    }
}