package com.example.mzrt.service;

import com.example.mzrt.model.Role;
import com.example.mzrt.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<Role> findByUserIdAndRole(int userId, String role) {
        return roleRepository.findByUserIdAndRole(userId, role);
    }

    public void save(Role role) {
        roleRepository.save(role);
    }

    public List<Role> findAllByUserId(int userId) {
        return roleRepository.findAllByUserId(userId);
    }
}
