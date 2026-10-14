package com.musicfever.music_fever.integration;

import com.musicfever.music_fever.model.Track;
import com.musicfever.music_fever.model.UserEntity;
import com.musicfever.music_fever.repository.UserRepository;
import com.musicfever.music_fever.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class UserIntegrationTest {
    private final UserRepository repository;
    private final UserService service;

    UserEntity[] usersTest = {
            new UserEntity("Test1", "pass1", "Test1@gmail.com"),
            new UserEntity("Test2", "pass2", "Test2@gmail.com"),
            new UserEntity("Test3", "pass3", "Test3@gmail.com")
    };

    public UserIntegrationTest(UserRepository repository, UserService service) {
        this.repository = repository;
        this.service = service;
    }

    @BeforeEach
    void setup(){
        // Given
        repository.deleteAll();

        for (UserEntity userEntity : usersTest) {
            this.service.saveUser(userEntity);
        }
    }

    @Test
    void shouldReturnUsers_orEmptyOptional(){
        // When
        Optional<UserEntity>[] obtainedUsers = new Optional[3];
        for(int i = 0; i < obtainedUsers.length; i++){
            obtainedUsers[i] = this.service.findByUsername(this.usersTest[i].getUsername());
        }
        Optional<UserEntity> obtainedUserEmpty = this.service.findByUsername("Test5");

        // Then
        for(int i = 0; i < usersTest.length; i++){
            assertTrue(obtainedUsers[i].isPresent());
            assertEquals(this.usersTest[i], obtainedUsers[i].get());
        }
        assertTrue(obtainedUserEmpty.isEmpty());
    }
}
