package com.in_out.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
public class License implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int license_Id;
  
  private String licensekey;
  
  private LocalDate Startdate;
  
  private LocalDate expirydate;
  
  @OneToOne
  private User user;
  
  public License() {}
  
  public License(int license_Id, String licensekey, LocalDate startdate, LocalDate expirydate, User user) {
    this.license_Id = license_Id;
    this.licensekey = licensekey;
    this.Startdate = startdate;
    this.expirydate = expirydate;
    this.user = user;
  }
  
  public int getLicense_Id() {
    return this.license_Id;
  }
  
  public void setLicense_Id(int license_Id) {
    this.license_Id = license_Id;
  }
  
  public String getLicensekey() {
    return this.licensekey;
  }
  
  public void setLicensekey(String licensekey) {
    this.licensekey = licensekey;
  }
  
  public LocalDate getStartdate() {
    return this.Startdate;
  }
  
  public void setStartdate(LocalDate startdate) {
    this.Startdate = startdate;
  }
  
  public LocalDate getExpirydate() {
    return this.expirydate;
  }
  
  public void setExpirydate(LocalDate expirydate) {
    this.expirydate = expirydate;
  }
  
  public User getUser() {
    return this.user;
  }
  
  public void setUser(User user) {
    this.user = user;
  }
}