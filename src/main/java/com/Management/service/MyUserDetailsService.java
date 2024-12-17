
// package com.Management.service;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.Management.Model.User;
// import com.Management.repository.UserRepository;

// @Service
// public class MyUserDetailsService implements UserDetailsService {

//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//         // Find the user by email
//         User user = userRepository.findByEmail(email)
//                 .orElseThrow(() -> new UsernameNotFoundException("Email not found with email: " + email));

//         // Map the User entity to UserDetails
//         return org.springframework.security.core.userdetails.User.builder()
//                 .username(user.getEmail()) // Email as username
//                 .password(user.getPassword()) // User's password
//                 .roles(user.getRoles().toArray(new String[0]))
//                 .build();
//     }
// }
