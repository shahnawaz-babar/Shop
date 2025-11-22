package com.shop.smartshop.serviceimpl;

import com.shop.smartshop.entity.User;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + username));

        // Step 1: Copy roles from DB
        Set<com.shop.smartshop.enums.Role> roles = new HashSet<>(user.getRoles());

        // Step 2: If user.id == 1 → make sure ADMIN role is present
        if (user.getId() == 1L) {
            roles.add(com.shop.smartshop.enums.Role.ADMIN);
        } else {
            // Optional: ensure USER role is always present
            roles.add(com.shop.smartshop.enums.Role.USER);
        }

        // Step 3: Convert roles to String[] for Spring Security
//        String[] roleNames = roles.stream()
//                .map(Enum::name)
//                .toArray(String[]::new);  return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getEmail())
//                .password("TEMP")
//                .roles(roleNames)
//                .build();
        // Step 4: Return Spring Security User

        return new UserPrincipal(user);


    }
}
