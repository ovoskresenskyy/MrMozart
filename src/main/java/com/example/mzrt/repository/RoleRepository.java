package com.example.mzrt.repository;

import com.example.mzrt.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

    List<Role> findAllByUserId(int id);
    Optional<Role> findByUserIdAndRole(int id, String role);
}


