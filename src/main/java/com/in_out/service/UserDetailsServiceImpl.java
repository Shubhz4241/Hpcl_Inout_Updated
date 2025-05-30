package com.in_out.service;


import com.in_out.model.User;
import com.in_out.repo.UserRepository;
import com.in_out.service.CustomUserdetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;
  
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = this.userRepository.findbyuserName(username);
    if (user == null)
      throw new UsernameNotFoundException("User not found"); 
    return (UserDetails)new CustomUserdetails(user);
  }
}
