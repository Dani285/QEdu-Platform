package org.backend.qedu.repo;

import org.backend.qedu.entities.User;
import org.backend.qedu.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String username);

    long countByRoles(Roles roles);
}
