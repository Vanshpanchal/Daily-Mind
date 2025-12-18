package spring.project.Daily.Mind.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import spring.project.Daily.Mind.entity.Users;
import spring.project.Daily.Mind.repository.UserRepository;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

public class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }

//    @Disabled
    @Test
    void loadUserByUsernameTest(){
        when(userRepository.findByusername(ArgumentMatchers.anyString())).thenReturn(Users.builder().username("sam").password("7485969").roles(new ArrayList<>()).build());
        UserDetails user = userDetailsService.loadUserByUsername("sam");

        Assertions.assertNotNull(user);
        Assertions.assertEquals("sam", user.getUsername());
    }
}

