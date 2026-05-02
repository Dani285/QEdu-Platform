package org.backend.qedu.repo;

import org.backend.qedu.entities.User;
import org.backend.qedu.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserRepo extends JpaRepository {
    Optional<User> findByUserName(String username);
    long countByRoles(Roles roles);
}
