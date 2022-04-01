package com.sahilasopa.authentication.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void userByEmailExists() {
        User user = new User(
                "test",
                "test@test.com",
                "B!gFatStrongP@ssw0rd"
        );
        underTest.save(user);
        User found = underTest.findUserByEmail("test@test.com");
        assertThat(found).isNotNull();
    }

    @Test
    void userByEmailDoesNotExists() {
        User found = underTest.findUserByEmail("test@test.com");
        assertThat(found).isNull();
    }
}