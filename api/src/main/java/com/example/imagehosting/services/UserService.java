package com.example.imagehosting.services;

import com.example.imagehosting.config.AppConstants;
import com.example.imagehosting.config.ApplicationUserDetails;
import com.example.imagehosting.dto.RegistrationRequest;
import com.example.imagehosting.entity.User;
import com.example.imagehosting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.imagehosting.config.AppConstants.DEFAULT_STORAGE_IN_BYTES;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        userOptional.orElseThrow(() -> new UsernameNotFoundException("User name [" + username + "] not found."));
        return new ApplicationUserDetails(userOptional.get());
    }

    public User processRegistrationRequest(RegistrationRequest registrationRequest) {
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setFreeSpaceAvailaible(DEFAULT_STORAGE_IN_BYTES);
        user.setRoles("ROLE_USER");

        return userRepository.save(user);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new UsernameNotFoundException("No user found with username: "+ username));
    }

    public boolean userHasFreeSpace(long size) {
        User currentUser = getCurrentUser();
        return (int) size < currentUser.getFreeSpaceAvailaible();
    }

    public void updateUserStorage(Integer size) {
        User currentUser = getCurrentUser();
        currentUser.setFreeSpaceAvailaible(currentUser.getFreeSpaceAvailaible() - size);
        userRepository.save(currentUser);
    }
}
