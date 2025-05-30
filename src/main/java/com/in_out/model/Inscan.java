package com.in_out.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
public class Inscan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long ofcid;

	private String details;

	private String department;

	@Column(name = "sub_department")
	private String subDepartment;

	private String name;

	private String contractor;

	private String firmName;

	@Column(name = "aadharNumber")
	private String aadharNumber;

	private String mobile;

	private String address;

	private String mainGateSatus;

	@Temporal(TemporalType.TIMESTAMP)
	private Date entryDateTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date exitDateTime;

	public String getMainGateSatus() {
		return this.mainGateSatus;
	}

	public void setMainGateSatus(String mainGateSatus) {
		this.mainGateSatus = mainGateSatus;
	}

	@PrePersist
	protected void onCreate() {
		this.entryDateTime = new Date();
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDetails() {
		return this.details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAadharNumber() {
		return this.aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getEntryDateTime() {
		return this.entryDateTime;
	}

	public void setEntryDateTime(Date entryDateTime) {
		this.entryDateTime = entryDateTime;
	}

	public Date getExitDateTime() {
		return this.exitDateTime;
	}

	public void setExitDateTime(Date exitDateTime) {
		this.exitDateTime = exitDateTime;
	}

	public Long getOfcid() {
		return this.ofcid;
	}

	public void setOfcid(Long ofcid) {
		this.ofcid = ofcid;
	}

	public String getDepartment() {
		return this.department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getSub_department() {
		return this.subDepartment;
	}

	public void setSub_department(String sub_department) {
		this.subDepartment = sub_department;
	}

	public String getSubDepartment() {
		return subDepartment;
	}

	public void setSubDepartment(String subDepartment) {
		this.subDepartment = subDepartment;
	}

	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	public String getFirmName() {
		return firmName;
	}

	public void setFirmName(String firmName) {
		this.firmName = firmName;
	}

}
