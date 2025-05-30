package com.in_out.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.sql.Date;
import java.time.LocalDate;

@Entity
public class VisitorTokenId {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long visitorId;

	private String aadharNumber;

	private String fullName;

	private String mobileNumber;

	private String address;

	private String whom;

	private String purpose;

	private Date date;

	private String qr;

	private String imageName;
	
	private Long currSrNo;

	@PrePersist
	@PreUpdate
	private void generateQrCode() {
		this.qr = "VS/HPNSK/" + (visitorId != null ? visitorId : "UNKNOWN");
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVisitorId() {
		return this.visitorId;
	}

	public void setVisitorId(Long visitorId) {
		this.visitorId = visitorId;
		this.qr = "VS/HPNSK/" + visitorId; // Ensure QR is updated when visitorId changes

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

	public Date getDate() {
		return this.date;
	}

	public void setDate(LocalDate localDate) {
		this.date = Date.valueOf(localDate);
	}

	public String getQr() {
		return this.qr;
	}

	public void setQr(String qr) {
		this.qr = qr;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public Long getCurrSrNo() {
		return currSrNo;
	}

	public void setCurrSrNo(Long currSrNo) {
		this.currSrNo = currSrNo;
	}

	
}
