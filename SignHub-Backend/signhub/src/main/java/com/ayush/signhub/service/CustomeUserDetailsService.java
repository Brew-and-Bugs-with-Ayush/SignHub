package com.ayush.signhub.service;

import com.ayush.signhub.domain.entity.User;
import com.ayush.signhub.domain.entity.UserPrincipal;
import com.ayush.signhub.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomeUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("Email not found in the Database" + email));

        return new UserPrincipal(user);
    }
}
