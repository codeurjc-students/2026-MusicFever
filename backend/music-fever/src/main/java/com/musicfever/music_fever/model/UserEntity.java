package com.musicfever.music_fever.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String username;
    private String password;
    private String email;

    private String profilePicture;

    @ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;


    public UserEntity() {
        /* Constructor used by the JPA interface. */
        this.roles = List.of("USER");
    }

    /* Constructor without the profile picture of the user. */
    public UserEntity(String username, String password, String email) {
        if ((!username.isBlank()) && (!password.isBlank()) && (!email.isBlank())) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.roles = List.of("USER");
        }
    }

    /* Constructor without the profile picture of the user and letting enter the roles*/
    public UserEntity(String username, String password, String email, String... roles) {
        if ((!username.isBlank()) && (!password.isBlank()) && (!email.isBlank())) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.roles = List.of(roles);
        }
    }

    /* Constructor with the profile picture of the user. */
    public UserEntity(String username, String password, String email, String country, String picture) {
        if ((!username.isBlank()) && (!password.isBlank()) && (!email.isBlank())) {
            this.username = username;
            this.password = password;
            this.email = email;
        }
        this.profilePicture = picture;
    }
}
