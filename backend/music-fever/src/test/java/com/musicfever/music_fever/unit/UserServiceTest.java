package com.musicfever.music_fever.unit;

import com.musicfever.music_fever.config.jwt.LoginRequest;
import com.musicfever.music_fever.exception.UserAlreadyExistsException;
import com.musicfever.music_fever.exception.UserNotFoundException;
import com.musicfever.music_fever.model.UserEntity;
import com.musicfever.music_fever.repository.UserRepository;
import com.musicfever.music_fever.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("unit")
public class UserServiceTest {
    UserService service;
    PasswordEncoder encoder;

    UserRepository repository;

    UserEntity[] usersTest = new UserEntity[3];
    LoginRequest[] requestsTest = new LoginRequest[3];

    @BeforeEach
    void setup(){
        // Given
        repository = mock(UserRepository.class);
        encoder = mock(PasswordEncoder.class);
        this.service = new UserService(repository, encoder);

        usersTest[0] = new UserEntity("Test1", "pass1", "Test1@gmail.com");
        usersTest[1] = new UserEntity("Test2", "pass2", "Test2@gmail.com");
        usersTest[2] = new UserEntity("Test3", "pass3", "Test3@gmail.com");

        requestsTest[0] = new LoginRequest("Test1@gmail.com", "Test1","pass1");
        requestsTest[1] = new LoginRequest("Test2@gmail.com", "Test2","pass2");
        requestsTest[2] = new LoginRequest("Test3@gmail.com", "Test3","pass3");
    }

    @Test
    @DisplayName("Save and find users successfully")
    void saveAndFindUsers_successfully(){
        // When
        when(repository.findByUsername(any(String.class)))
                .thenReturn(Optional.of(usersTest[0]))
                .thenReturn(Optional.of(usersTest[1]))
                .thenReturn(Optional.of(usersTest[2]));

        // Then
        this.service.saveUser(usersTest[0]);
        this.service.saveUser(usersTest[1]);
        this.service.saveUser(usersTest[2]);

        verify(this.repository, times(3)).save(any(UserEntity.class));

        for(int i = 0; i < 3; i++){
            Optional<UserEntity> opUser = this.service.findByUsername("Test" + (i+1));
            assertTrue(opUser.isPresent());
            assertEquals(usersTest[i], opUser.get());
        }
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Email not found, empty, failedtest@gmail.com, true, -1",
            "Username not found, Test2, Test2@gmail.com, true, 1",
            "Login with email,,Test1@gmail.com, false, 0",
            "Login with username, Test3, Test3@gmail.com, false, 2"
    })
    @DisplayName("Verifies an user that wants to login")
    void verifyUserLogin_checkIfUserExists(String testName, String username, String email, boolean fails, int i){
        // Given
        LoginRequest request = new LoginRequest(email, username, "pass" + (i+1));
        if (username != null && username.equals("empty")){
            request.setUsername("");
        }

        // When
        when(this.repository.findByUsername("Test3")).thenReturn(Optional.of(usersTest[2]));
        when(this.repository.findByEmail("Test1@gmail.com")).thenReturn(Optional.of(usersTest[0]));
        when(this.repository.findByEmail("failedtest@gmail.com")).thenReturn(Optional.empty());
        when(this.repository.findByUsername("Test2")).thenReturn(Optional.empty());

        // Then
        if (fails){
            assertThrows(UserNotFoundException.class, () -> {
               this.service.resolveLoginIdentifier(request);
            });
        } else {
            LoginRequest realRequest = this.service.resolveLoginIdentifier(request);
            assertEquals(requestsTest[i].getUsername(), realRequest.getUsername());
            assertEquals(requestsTest[i].getEmail(), realRequest.getEmail());
            assertEquals(requestsTest[i].getPassword(), realRequest.getPassword());
        }
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "Email found, Test3, failedtest@gmail.com, true, 3",
            "Username found, Test4, Test2@gmail.com, true, 1",
            "Register successfully,Test1,Test1@gmail.com, false, 0",
    })
    @DisplayName("Verifies an user that wants to login")
    void verifyUserRegistration_checkIfUserDoesNotExists(
            String testName, String username, String email, boolean fails, int i){

        // Given
        LoginRequest request = new LoginRequest(email, username, "pass" + (i+1));

        // When
        when(this.repository.findByUsername("Test3")).thenReturn(Optional.of(usersTest[2]));
        when(this.repository.findByEmail("Test1@gmail.com")).thenReturn(Optional.empty());
        when(this.repository.findByUsername("Test1")).thenReturn(Optional.empty());
        when(this.repository.findByEmail("Test2@gmail.com")).thenReturn(Optional.of(usersTest[1]));
        when(this.repository.findByUsername("Test4")).thenReturn(Optional.empty());
        when(this.encoder.encode("pass1")).thenReturn("string");

        // Then
        if (fails){
            assertThrows(UserAlreadyExistsException.class, () -> {
                this.service.registerUser(request);
            });
        } else {
            this.service.registerUser(request);
            verify(this.repository, times(1)).findByUsername(anyString());
            verify(this.repository, times(1)).findByEmail(anyString());
            verify(this.encoder, times(1)).encode(anyString());
        }
    }
}
