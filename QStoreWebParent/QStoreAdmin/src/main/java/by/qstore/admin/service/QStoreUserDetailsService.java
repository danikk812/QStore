package by.qstore.admin.service;

import by.qstore.admin.entity.security.QStoreUserDetails;
import by.qstore.admin.repository.UserRepository;
import by.qstore.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class QStoreUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userByEmail = userRepository.getUserByEmail(email);
        if (userByEmail != null ) {
            return new QStoreUserDetails(userByEmail); 
        }
        throw new UsernameNotFoundException("User with email " + email + " not found");
    }
}
