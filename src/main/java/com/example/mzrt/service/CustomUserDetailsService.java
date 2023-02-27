package com.example.mzrt.service;

import com.example.mzrt.model.User;
import com.example.mzrt.repository.RoleRepository;
import com.example.mzrt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository,
                                    RoleRepository repository) {

        this.userRepository = userRepository;
        this.roleRepository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getPassword(),
                    roleRepository.findAllByUserId(user.getId()));
        } else throw new UsernameNotFoundException("Invalid username or password.");
    }
}
