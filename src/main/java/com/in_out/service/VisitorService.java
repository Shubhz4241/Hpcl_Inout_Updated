package com.in_out.service;


import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.model.Visitor;
import com.in_out.model.VisitorTokenId;
import com.in_out.repo.VisitorRepository;
import com.in_out.repo.VisitorTokenIdRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VisitorService {
  @Autowired
  private VisitorRepository visitorRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  @Autowired
  private VisitorTokenIdRepository visitorTokenRepository;
  
  @Autowired
  private VisitorTokenIdService visitorTokenIdService;
  
	/*
	 * public List<Visitor> addVisitor(List<Integer> visitorData) { List<Visitor>
	 * visitor = visitorData.stream().map(intValue -> new Visitor()).toList();
	 * return this.visitorRepository.saveAll(visitor); }
	 */
  public List<Visitor> addVisitor(List<Integer> visitorData) {
	    if (visitorData.stream().anyMatch(i -> i <= 0)) {
	        throw new IllegalArgumentException("Visitor data cannot contain negative or zero values"); // Fixed Defect 1
	    }
	    List<Visitor> visitor = visitorData.stream().map(intValue -> new Visitor()).toList();
	    return this.visitorRepository.saveAll(visitor);
	}
  
  public List<Visitor> getAllVisitorDetails() {
    return this.visitorRepository.findAll();
  }
  
  public Optional<Visitor> getVisitorById(Long id) {
    return this.visitorRepository.findById(id);
  }
  
  public long countVisitorsWithFullNameNotNull() {
    return this.visitorRepository.countVisitorsWithFullNameNotNull();
  }
  
  public Visitor addVisitor(Visitor visitor) {
    Visitor existingVisitor = this.visitorRepository.findByAadharNumber(visitor.getAadharNumber());
    if (existingVisitor != null)
      throw new IllegalArgumentException("An Packed with the same Aadhar number already exists"); 
    return (Visitor)this.visitorRepository.save(visitor);
  }
  
  public Visitor updateVisitor(Long id, Visitor updatedVisitor) {
    Optional<Visitor> existingVisitor = this.visitorRepository.findById(id);
    if (existingVisitor.isPresent()) {
      Visitor visitorToUpdate = existingVisitor.get();
      Visitor existingVisitorWithNewAadhar = this.visitorRepository.findByAadharNumber(updatedVisitor.getAadharNumber());
      if (existingVisitorWithNewAadhar != null && !existingVisitorWithNewAadhar.getId().equals(id))
        throw new IllegalArgumentException("An Visitor with the same Aadhar number already exists"); 
      visitorToUpdate.setAadharNumber(updatedVisitor.getAadharNumber());
      visitorToUpdate.setFullName(updatedVisitor.getFullName());
      visitorToUpdate.setMobileNumber(updatedVisitor.getMobileNumber());
      visitorToUpdate.setAddress(updatedVisitor.getAddress());
      visitorToUpdate.setWhom(updatedVisitor.getWhom());
      visitorToUpdate.setPurpose(updatedVisitor.getPurpose());
      visitorToUpdate.setImageName(updatedVisitor.getImageName());
      visitorToUpdate.setVisitDate(LocalDateTime.now());
      visitorToUpdate.setRegular(updatedVisitor.isRegular());
      return (Visitor)this.visitorRepository.save(visitorToUpdate);
    } 
    throw new IllegalArgumentException("Visitor not found");
  }
  
  public Visitor deleteVisitorDetails(Long id) {
    Optional<Visitor> existingVisitor = this.visitorRepository.findById(id);
    if (existingVisitor.isPresent()) {
      Visitor visitorToUpdate = existingVisitor.get();
      visitorToUpdate.setAadharNumber(null);
      visitorToUpdate.setFullName(null);
      visitorToUpdate.setMobileNumber(null);
      visitorToUpdate.setAddress(null);
      visitorToUpdate.setWhom(null);
      visitorToUpdate.setPurpose(null);
      visitorToUpdate.setImageName(null);
      visitorToUpdate.setVisitDate(null);
      visitorToUpdate.setRegular(false);
      return (Visitor)this.visitorRepository.save(visitorToUpdate);
    } 
    throw new IllegalArgumentException("Visitor not found");
  }
  
  public String processAndSaveDetails(Long visitorId) {
    if (this.visitorRepository == null || this.inscanService == null)
      throw new IllegalStateException("Visitor repository or Inscan service not initialized"); 
    Optional<Visitor> optionalVisitor = this.visitorRepository.findById(visitorId);
    if (optionalVisitor.isPresent()) {
      Visitor visitor = optionalVisitor.get();
      StringBuilder detailsBuilder = (new StringBuilder("VS/HPNSK/")).append(visitor.getId());
      String department = "Visitor";
      String sub_department = "VS";
      String details = "VS/HPNSK/";
      Long ofcid = visitor.getId();
      String name = visitor.getFullName();
      String aadharNumber = visitor.getAadharNumber();
      String mobile = visitor.getMobileNumber();
      String address = visitor.getAddress();
      String contractor=null;
      String firmName=null;
      
      Inscan inscan = this.inscanService.findByAadhar(aadharNumber);
      if (inscan == null) {
        String mainGateStatus = "Y";
        this.inscanService.saveDetailsToInscan(details, name, aadharNumber, mobile, address, ofcid, department, sub_department, mainGateStatus,contractor,firmName);
        return "In";
      } 
      String mainGateStatus = "N";
      Licensegate licensegate = this.licenseGateService.findByAadhar(aadharNumber);
      boolean isLicenseGateIn = false;
      if (licensegate == null || (licensegate != null && licensegate.getExitDateTime() != null))
        isLicenseGateIn = true; 
      if (!isLicenseGateIn)
        return "Please exit from License gate"; 
      if (inscan.getExitDateTime() != null) {
        this.inscanService.saveDetailsToInscan(details, name, aadharNumber, mobile, address, ofcid, department, sub_department, mainGateStatus,contractor,firmName);
        return "Scan In";
      } 
      this.inscanService.updateDetailsToInscan(inscan);
      return "Scan Out";
    } 
    return "Officer not found";
  }
  
  public Visitor getDetailsByAadharNumber(String aadharNumber) {
    return this.visitorRepository.findByAadharNumber(aadharNumber);
  }
  
  public String getFullName(Long entityId) {
    Visitor visitor = this.visitorRepository.findById(entityId).orElse(null);
    if (visitor != null) {
      String fullName = visitor.getFullName();
      return fullName;
    } 
    return "Unknown Visitor";
  }
  
  public String processAndSaveLicenseGateDetails(Long visitorId) {
    if (this.visitorRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Visitor repository or LicenseGateService service not initialized"); 
    Optional<Visitor> optionalVisitor = this.visitorRepository.findById(visitorId);
    if (optionalVisitor.isPresent()) {
      Visitor visitor = optionalVisitor.get();
      StringBuilder detailsBuilder = (new StringBuilder("VS/HPNSK/")).append(visitor.getId());
      String department = "Visitor";
      String sub_department = "VS";
      String details = detailsBuilder.toString();
      Long ofcid = visitor.getId();
      String name = visitor.getFullName();
      String aadharNumber = visitor.getAadharNumber();
      String mobile = visitor.getMobileNumber();
      String address = visitor.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licensegate = this.licenseGateService.findByAadhar(aadharNumber);
      if (licensegate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, ofcid, department, sub_department,contractor,firmName);
        return "IN";
      } 
      if (licensegate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, ofcid, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licensegate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Visitor not found");
  }
  
  public void storeVisitorDetailsIntoToken(Visitor visitor) {
    VisitorTokenId visitorToken = new VisitorTokenId();
    
///////////////////////
visitorToken.setCurrSrNo(visitorTokenIdService.generateNextSrNo());
////////////////////////

    visitorToken.setVisitorId(visitor.getId());
    visitorToken.setFullName(visitor.getFullName());
    visitorToken.setMobileNumber(visitor.getMobileNumber());
    visitorToken.setAddress(visitor.getAddress());
    visitorToken.setWhom(visitor.getWhom());
    visitorToken.setPurpose(visitor.getPurpose());
    visitorToken.setAadharNumber(visitor.getAadharNumber());
    visitorToken.setDate(LocalDate.now());
    visitorToken.setImageName(visitor.getImageName());
    this.visitorTokenRepository.save(visitorToken);
  }
  
  public boolean restrictUser(Long id) {
    Optional<Visitor> optionalVisitor = this.visitorRepository.findById(id);
    if (optionalVisitor.isPresent()) {
      Visitor visitor = optionalVisitor.get();
      visitor.setRestricted(true);
      this.visitorRepository.save(visitor);
      return true;
    } 
    return false;
  }
  
  public boolean unrestrictUser(Long visitorId) {
    Optional<Visitor> optionalVisitor = this.visitorRepository.findById(visitorId);
    if (optionalVisitor.isPresent()) {
      Visitor visitor = optionalVisitor.get();
      visitor.setRestricted(false);
      this.visitorRepository.save(visitor);
      return true;
    } 
    return false;
  }
  
  public boolean restrictornot(Long entityId) {
    Visitor visitor = this.visitorRepository.findById(entityId).orElse(null);
    if (visitor != null) {
      boolean restricted = visitor.isRestricted();
      return restricted;
    } 
    return ("Unknown Visitor" != null);
  }
}
