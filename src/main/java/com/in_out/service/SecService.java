package com.in_out.service;


import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.model.Sec;
import com.in_out.repo.SecRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecService {
  @Autowired
  private SecRepository secRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
	/*
	 * public List<Sec> addSec(List<Integer> secData) { List<Sec> secs =
	 * secData.stream().map(intValue -> new Sec()).toList(); return
	 * this.secRepository.saveAll(secs); }
	 */
  public List<Sec> addSec(List<Integer> secData) {
	    if (secData.stream().anyMatch(i -> i <= 0)) {
	        throw new IllegalArgumentException("Sec data cannot contain negative or zero values"); // Fixed Defect 1
	    }
	    List<Sec> secs = secData.stream().map(intValue -> new Sec()).toList();
	    return this.secRepository.saveAll(secs);
	}
  
  public List<Sec> getAllSecDetails() {
    return this.secRepository.findAll();
  }
  
  public Optional<Sec> getSecById(Long id) {
    return this.secRepository.findById(id);
  }
  
  public Sec addSec(Sec sec) {
    Sec existingSec = this.secRepository.findByAadharNumber(sec.getAadharNumber());
    if (existingSec != null)
      throw new IllegalArgumentException("An sec with the same Aadhar number already exists"); 
    return (Sec)this.secRepository.save(sec);
  }
  
  public Sec updateSec(Long id, Sec updatedSec) {
    Optional<Sec> existingSec = this.secRepository.findById(id);
    if (existingSec.isPresent()) {
      Sec secToUpdate = existingSec.get();
      Sec existingSecWithNewAadhar = this.secRepository.findByAadharNumber(updatedSec.getAadharNumber());
      if (existingSecWithNewAadhar != null && !existingSecWithNewAadhar.getId().equals(id))
        throw new IllegalArgumentException("An Sec with the same Aadhar number already exists"); 
      secToUpdate.setAadharNumber(updatedSec.getAadharNumber());
      secToUpdate.setFullName(updatedSec.getFullName());
      secToUpdate.setMobileNumber(updatedSec.getMobileNumber());
      secToUpdate.setAddress(updatedSec.getAddress());
      secToUpdate.setFirmName(updatedSec.getFirmName());
      return (Sec)this.secRepository.save(secToUpdate);
    } 
    throw new IllegalArgumentException("Sec not found");
  }
  
  public Sec deleteSecDetails(Long id) {
    Optional<Sec> existingSec = this.secRepository.findById(id);
    if (existingSec.isPresent()) {
      Sec secToUpdate = existingSec.get();
      secToUpdate.setAadharNumber(null);
      secToUpdate.setFullName(null);
      secToUpdate.setMobileNumber(null);
      secToUpdate.setAddress(null);
      return (Sec)this.secRepository.save(secToUpdate);
    } 
    throw new IllegalArgumentException("Sec not found");
  }
  
  public String processAndSaveDetails(Long secId) {
    if (this.secRepository == null || this.inscanService == null)
      throw new IllegalStateException("Sec repository or Inscan service not initialized"); 
    Optional<Sec> optionalSec = this.secRepository.findById(secId);
    if (optionalSec.isPresent()) {
      Sec sec = optionalSec.get();
      StringBuilder detailsBuilder = (new StringBuilder("SEC/HPNSK/")).append(sec.getId());
      String department = "Operation";
      String sub_department = "SEC";
      String details = "SEC/HPNSK/";
      Long ofcid = sec.getId();
      String name = sec.getFullName();
      String aadharNumber = sec.getAadharNumber();
      String mobile = sec.getMobileNumber();
      String address = sec.getAddress();
      String contractor=null;
      String firmName=null;
      
      
      Inscan inscan = this.inscanService.findByAadhar(aadharNumber);
      if (inscan == null) {
        String str = "Y";
        this.inscanService.saveDetailsToInscan(details, name, aadharNumber, mobile, address, ofcid, department, sub_department, str,contractor,firmName);
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
    throw new IllegalArgumentException("Sec not found");
  }
  
  public Sec getDetailsByAadharNumber(String aadharNumber) {
    return this.secRepository.findByAadharNumber(aadharNumber);
  }
  
  public String getFullName(Long entityId) {
    Sec sec = this.secRepository.findById(entityId).orElse(null);
    if (sec != null) {
      String fullName = sec.getFullName();
      return fullName;
    } 
    return "Unknown Sec";
  }
  
  public String processAndSaveLicenseGateDetails(Long secId) {
    if (this.secRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Sec repository or LicenseGateService service not initialized"); 
    Optional<Sec> optionalSec = this.secRepository.findById(secId);
    if (optionalSec.isPresent()) {
      Sec sec = optionalSec.get();
      StringBuilder detailsBuilder = (new StringBuilder("SEC/HPNSK/")).append(sec.getId());
      String department = "Operation";
      String sub_department = "SEC";
      String details = detailsBuilder.toString();
      Long ofcId = sec.getId();
      String name = sec.getFullName();
      String aadharNumber = sec.getAadharNumber();
      String mobile = sec.getMobileNumber();
      String address = sec.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licenseGate = this.licenseGateService.findByAadhar(aadharNumber);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, secId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, secId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Sec not found");
  }
}
