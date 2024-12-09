package com.ecommerce.project.repositories;

import com.ecommerce.project.model.AppRoles;
import com.ecommerce.project.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRoles appRoles);


    //Optional<Role> findByRoleName(String roleName);
}
