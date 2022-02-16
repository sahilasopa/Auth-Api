package com.sahilasopa.chat.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    User findUserByUsername(String username);

    User findUserByUsernameAndPassword(String username, String password);
}
