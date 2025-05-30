package com.in_out.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.io.Serializable;

@Entity
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int userId;
  
  private String userName;
  
  private String Password;
  
  private String Role;
  
  @OneToOne
  private License license;
  
  public User() {}
  
  public User(int userId, String userName, String password, String role, License license) {
    this.userId = userId;
    this.userName = userName;
    this.Password = password;
    this.Role = role;
    this.license = license;
  }
  
  public int getUserId() {
    return this.userId;
  }
  
  public void setUserId(int userId) {
    this.userId = userId;
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
  
  public String getPassword() {
    return this.Password;
  }
  
  public void setPassword(String password) {
    this.Password = password;
  }
  
  public String getRole() {
    return this.Role;
  }
  
  public void setRole(String role) {
    this.Role = role;
  }
  
  public License getLicense() {
    return this.license;
  }
  
  public void setLicense(License license) {
    this.license = license;
  }
}
