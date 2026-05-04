package org.backend.qedu.repo;

import org.backend.qedu.entities.User;
import org.backend.qedu.model.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String username);

    long countByClassGroups(String classGroups);

    List<User> findByRoles(Roles roles);

    List<User> findByRolesAndClassGroups(Roles roles, String classGroups);

    long countByRoles(Roles roles);
}
