package com.in_out.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "Visitor")
public class Visitor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(unique = true, name = "aadharNumber", length = 12)
  private String aadharNumber;
  
  private String fullName;
  
  private String mobileNumber;
  
  private String address;
  
  private String whom;
  
  private String purpose;
  
  private String visi;
  
  private String qr;
  
  private String imageName;
  
  private boolean isRegular;
  
  private boolean restricted;
  
  @Column(name = "visit_date")
  private LocalDateTime visitDate;
  
  public Visitor() {
    this.visi = "Visitor-Visitor";
    this.qr = "VS/HPNSK";
  }
  
  public Visitor(Long id, String aadharNumber, String fullName, String mobileNumber, String address, String whom, String purpose, String visi, String imageName) {
    this.id = id;
    this.aadharNumber = aadharNumber;
    this.fullName = fullName;
    this.mobileNumber = mobileNumber;
    this.address = address;
    this.whom = whom;
    this.purpose = purpose;
    this.visi = visi;
    this.imageName = imageName;
  }
  
  public Long getId() {
    return this.id;
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  
  public String getAadharNumber() {
    return this.aadharNumber;
  }
  
  public void setAadharNumber(String aadharNumber) {
    this.aadharNumber = aadharNumber;
  }
  
  public String getFullName() {
    return this.fullName;
  }
  
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
  
  public String getMobileNumber() {
    return this.mobileNumber;
  }
  
  public void setMobileNumber(String mobileNumber) {
    this.mobileNumber = mobileNumber;
  }
  
  public String getAddress() {
    return this.address;
  }
  
  public void setAddress(String address) {
    this.address = address;
  }
  
  public String getWhom() {
    return this.whom;
  }
  
  public void setWhom(String whom) {
    this.whom = whom;
  }
  
  public String getPurpose() {
    return this.purpose;
  }
  
  public void setPurpose(String purpose) {
    this.purpose = purpose;
  }
  
  public String getVisi() {
    return this.visi;
  }
  
  public void setVisi(String visi) {
    this.visi = visi;
  }
  
  public String getImageName() {
    return this.imageName;
  }
  
  public void setImageName(String imageName) {
    this.imageName = imageName;
  }
  
  public boolean isRegular() {
    return this.isRegular;
  }
  
  public void setRegular(boolean isRegular) {
    this.isRegular = isRegular;
  }
  
  public LocalDateTime getVisitDate() {
    return this.visitDate;
  }
  
  public void setVisitDate(LocalDateTime visitDate) {
    this.visitDate = visitDate;
  }
  
  public String getQr() {
    return this.qr;
  }
  
  public void setQr(String qr) {
    this.qr = qr;
  }
  
  public boolean isRestricted() {
    return this.restricted;
  }
  
  public void setRestricted(boolean restricted) {
    this.restricted = restricted;
  }
}
