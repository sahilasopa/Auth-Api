package com.sahilasopa.authentication.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    User findUserByUsername(String username);
    @Query("" +
            "SELECT CASE WHEN COUNT(user) > 0 THEN " +
            "TRUE ELSE FALSE END " +
            "FROM User user " +
            "WHERE user.email = ?1"
    )
    Boolean userExistsByEmail(String email);
    @Query("" +
            "SELECT CASE WHEN COUNT(user) > 0 THEN " +
            "TRUE ELSE FALSE END " +
            "FROM User user " +
            "WHERE user.username = ?1"
    )
    Boolean userExistsByUsername(String username);
}

