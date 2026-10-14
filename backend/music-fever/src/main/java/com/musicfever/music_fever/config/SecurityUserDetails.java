package com.musicfever.music_fever.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.musicfever.music_fever.model.UserEntity;
import com.musicfever.music_fever.repository.UserRepository;

@Service 
public class SecurityUserDetails implements UserDetailsService{
    private final UserRepository userRepository;

    public SecurityUserDetails(UserRepository repository){
        this.userRepository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        UserEntity user = userRepository.findByUsername(username).orElseThrow( () -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> roles = new ArrayList<>();
        for (String role: user.getRoles()){
            roles.add(new SimpleGrantedAuthority("ROLE_" + role));
        }

        return new User(user.getUsername(), user.getPassword(), roles);
    }
}
