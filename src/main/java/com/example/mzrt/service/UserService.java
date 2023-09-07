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
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User incomingData, String role) {

        incomingData.setPassword(getPassword(incomingData));

        User savedUser = userRepository.save(incomingData);
        addRole(role);

        return savedUser;
    }

    private String getPassword(User userDTO) {
        if (!userDTO.getPassword().isEmpty()) {
            return passwordEncoder.encode(userDTO.getPassword());
        }
        return findById(userDTO.getId()).getPassword();
    }

    public User findByToken(String token) {
        return userRepository.findByToken(token).orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
    }

    private void addRole(String role) {
        Optional<Role> existedRole = roleService.findByRole(role);
        if (existedRole.isEmpty()) roleService.save(Role.builder()
                .role(role)
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