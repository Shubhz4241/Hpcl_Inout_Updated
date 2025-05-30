package com.in_out.service;


import com.in_out.model.User;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserdetails implements UserDetails {
  @Autowired
  private User user;
  
  public CustomUserdetails(User user) {
    this.user = user;
  }
  
  public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(this.user.getRole());
    return (Collection)Arrays.asList((Object[])new SimpleGrantedAuthority[] { grantedAuthority });
  }
  
  public String getPassword() {
    return this.user.getPassword();
  }
  
  public String getUsername() {
    return this.user.getUserName();
  }
  
  public boolean isAccountNonExpired() {
    return true;
  }
  
  public boolean isAccountNonLocked() {
    return true;
  }
  
  public boolean isCredentialsNonExpired() {
    return true;
  }
  
  public boolean isEnabled() {
    return true;
  }
}
