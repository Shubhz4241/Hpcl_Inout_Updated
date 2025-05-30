package com.in_out.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Mathadi")
public class Mathadi {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  
  @Column(unique = true, name = "aadharNumber", length = 12)
  private String aadharNumber;
  
  private String fullName;
  
  private String mobileNumber;
  
  private String address;
  
  private String  enumber;
  
  private String firmName;
  
  private String contractor;
  
  private String mtd;
  
  private String qr;
  
  public Mathadi() {
    this.mtd = "Operation-Mathadi";
    this.qr = "MT/HPNSK";
  }
 
public Mathadi(Long id, String aadharNumber, String fullName, String mobileNumber, String address, String enumber,
		String firmName, String contractor, String mtd, String qr) {
	super();
	this.id = id;
	this.aadharNumber = aadharNumber;
	this.fullName = fullName;
	this.mobileNumber = mobileNumber;
	this.address = address;
	this.enumber = enumber;
	this.firmName = firmName;
	this.contractor = contractor;
	this.mtd = mtd;
	this.qr = qr;
}

public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}

public String getAadharNumber() {
	return aadharNumber;
}

public void setAadharNumber(String aadharNumber) {
	this.aadharNumber = aadharNumber;
}

public String getFullName() {
	return fullName;
}

public void setFullName(String fullName) {
	this.fullName = fullName;
}

public String getMobileNumber() {
	return mobileNumber;
}

public void setMobileNumber(String mobileNumber) {
	this.mobileNumber = mobileNumber;
}

public String getAddress() {
	return address;
}

public void setAddress(String address) {
	this.address = address;
}

public String getEnumber() {
	return enumber;
}

public void setEnumber(String enumber) {
	this.enumber = enumber;
}

public String getFirmname() {
	return firmName;
}

public void setFirmname(String firmname) {
	this.firmName = firmname;
}

public String getContractor() {
	return contractor;
}

public void setContractor(String contractor) {
	this.contractor = contractor;
}

public String getMtd() {
	return mtd;
}

public void setMtd(String mtd) {
	this.mtd = mtd;
}

public String getQr() {
	return qr;
}

public void setQr(String qr) {
	this.qr = qr;
}
 public String getMathadi() {
	return this.mtd;
	 
 }
 public void setMathadi(String mtd) {
	 this.mtd=mtd;
 }
}

