package com.example.mzrt.repository;

import com.example.mzrt.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    List<User> findAll(Sort sort);
    Optional<User> findByEmail(String email);
    Optional<User> findByToken(String token);
}


