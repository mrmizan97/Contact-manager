package com.main.config;

import com.main.dao.UserRepository;
import com.main.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsSermviceIml implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.getUserByUserName(username);
       if (user==null){
           throw new UsernameNotFoundException("Could not found user.");
       }
       CustomUserDetails customUserDetails=new CustomUserDetails(user);
       return  customUserDetails;
    }
}
