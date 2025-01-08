package com.ecommerce.project.repository;

import com.ecommerce.project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);

    Boolean existsByUserEmail(String email);

    Boolean existsByUserName(String username);

    @Query("SELECT u.firstName FROM User u WHERE u.userId = ?1")
    Optional<String> findFirstNameByUserId(Long id);
    @Query("SELECT u.lastName FROM User u WHERE u.userId = ?1")
    Optional<String> findLastNameByUserId(Long id);
}
