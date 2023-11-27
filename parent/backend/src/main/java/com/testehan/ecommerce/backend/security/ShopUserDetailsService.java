package com.testehan.ecommerce.backend.security;

import com.testehan.ecommerce.backend.user.UserRepository;
import com.testehan.ecommerce.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;

// service used for the login
public class ShopUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(email);
        if (Objects.nonNull(user)) {
            return new ShopUserDetails(user);
        } else {
            throw new UsernameNotFoundException("User with email " + email + " does not exist");
        }
    }
}
