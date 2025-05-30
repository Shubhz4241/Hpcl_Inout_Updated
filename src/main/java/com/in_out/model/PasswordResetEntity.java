package com.in_out.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PasswordResetEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String resetPassword;
  
  private boolean passwordSet;
  
  public Long getId() {
    return this.id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getResetPassword() {
    return this.resetPassword;
  }
  
  public void setResetPassword(String resetPassword) {
    this.resetPassword = resetPassword;
  }
  
  public boolean isPasswordSet() {
    return this.passwordSet;
  }
  
  public void setPasswordSet(boolean passwordSet) {
    this.passwordSet = passwordSet;
  }
}
