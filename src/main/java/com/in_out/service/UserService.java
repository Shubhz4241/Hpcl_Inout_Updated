package com.in_out.service;


import com.in_out.model.License;
import com.in_out.model.User;
import com.in_out.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
  public void CreateUser(User user) {
    user.setPassword(this.passwordEncoder.encode(user.getPassword()));
    this.userRepository.save(user);
  }
  
  public void updateuser(User user) {
    User existinguser = (User)this.userRepository.getReferenceById(Integer.valueOf(user.getUserId()));
    String role = existinguser.getRole();
    if (!user.getUserName().equals(""))
      existinguser.setUserName(user.getUserName()); 
    if (!user.getPassword().equals(""))
      existinguser.setPassword(this.passwordEncoder.encode(user.getPassword())); 
    if (user.getRole() != null && !user.getRole().equals("0")) {
      existinguser.setRole(user.getRole());
    } else {
      existinguser.setRole(role);
    } 
    this.userRepository.save(existinguser);
  }
  
  public void updateprofile(User user) {
    if (user.getUserId() != 0) {
      if (this.userRepository.existsById(Integer.valueOf(user.getUserId()))) {
        User existingUser = (User)this.userRepository.findById(Integer.valueOf(user.getUserId())).orElseThrow(() -> new EntityNotFoundException("User with ID " + user.getUserId() + " not found"));
        existingUser.setUserName(user.getUserName());
        this.userRepository.save(existingUser);
      } else {
        throw new EntityNotFoundException("User with ID " + user.getUserId() + " not found");
      } 
    } else {
      System.out.println("Ignoring update for user with ID 0");
    } 
  }
  
  public String checkcheckLicenseStatus() {
    User admin = (User)this.userRepository.getReferenceById(Integer.valueOf(1));
    License license = admin.getLicense();
    if (license == null)
      return null; 
    if (LocalDate.now().equals(license.getExpirydate()))
      return "Expired"; 
    return "Valid";
  }
  
  public boolean deleteUserById(int userId) {
    if (userId != 0) {
      if (this.userRepository.existsById(Integer.valueOf(userId))) {
        this.userRepository.deleteById(Integer.valueOf(userId));
        return true;
      } 
      System.out.println("User with ID " + userId + " not found for deletion");
    } else {
      System.out.println("Ignoring deletion for user with ID 0");
    } 
    return false;
  }
}
