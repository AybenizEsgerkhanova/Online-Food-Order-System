package com.example.foodordersystem.service;

import com.example.foodordersystem.exception.UserNotFoundException;
import com.example.foodordersystem.model.dto.request.RegisterRequest;
import com.example.foodordersystem.model.entity.User;
import com.example.foodordersystem.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.foodordersystem.model.entity.User.Role.CUSTOMER;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Parollar uyğun gəlmir");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username artıq mövcuddur");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email artıq mövcuddur");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(CUSTOMER);

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user;
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
