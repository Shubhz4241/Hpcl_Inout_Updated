package com.in_out.config;


import java.time.LocalDate;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.in_out.model.License;
import com.in_out.model.User;
import com.in_out.repo.LicenseRepository;
import com.in_out.repo.UserRepository;

@Component
public class InitialDataloader implements ApplicationListener<ContextRefreshedEvent> {
  private final UserRepository userRepository;
  
  private final PasswordEncoder passwordEncoder;
  
  private final LicenseRepository licenseRepository;
  
  public InitialDataloader(UserRepository userRepository, PasswordEncoder passwordEncoder, LicenseRepository licenseRepository) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.licenseRepository = licenseRepository;
  }
  
  private static boolean defaultAdminUserCreated = false;
  
  public void onApplicationEvent(ContextRefreshedEvent event) {
    if (!defaultAdminUserCreated) {
      createDefaultAdminUser();
      defaultAdminUserCreated = true;
    } 
  }
  
  private void createDefaultAdminUser() {
    User admin = this.userRepository.findByUserName("admin");
    if (admin == null) {
      admin = new User();
      admin.setUserName("admin");
      admin.setPassword(this.passwordEncoder.encode("admin"));
      admin.setRole("admin");
      this.userRepository.save(admin);
    } 
    long licenseCount = this.licenseRepository.count();
    if (licenseCount == 0L) {
      License license = new License();
      license.setLicensekey(this.passwordEncoder.encode("License"));
      license.setExpirydate(LocalDate.of(2026, 5, 1));
      this.licenseRepository.save(license);
    } 
  }
}
