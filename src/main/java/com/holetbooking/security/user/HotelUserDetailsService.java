package com.holetbooking.security.user;

import com.holetbooking.model.User;
import com.holetbooking.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class HotelUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // ✅ constructor injection (no need for field @Autowired)
    public HotelUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return HotelUserDetails.buildUserDetails(user);
    }
}
