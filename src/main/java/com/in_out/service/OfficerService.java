package com.in_out.service;


import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.model.Officer;
import com.in_out.repo.OfficerRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
public class OfficerService {
  @Autowired
  private OfficerRepository officerRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  public OfficerService(OfficerRepository officerRepository) {
    this.officerRepository = officerRepository;
  }
  
  public List<Officer> addOfficer(List<Integer> operatorData) {
    List<Officer> transportor = operatorData.stream().map(intValue -> new Officer()).toList();
    return this.officerRepository.saveAll(transportor);
  }
  
  public List<Officer> getAllOfficerDetails() {
    return this.officerRepository.findAll();
  }
  
  public List<String> getAllOfficerFullNames() {
    return this.officerRepository.findFullNames();
  }
  
  public Optional<Officer> getOfficerById(Long id) {
    return this.officerRepository.findById(id);
  }
  
  public Officer updateOfficer(Long id, Officer updatedOfficer) {
	    Optional<Officer> existingOfficer = this.officerRepository.findById(id);
	    if (existingOfficer.isPresent()) {
	        Officer officerToUpdate = existingOfficer.get();
	        Officer existingOfficerWithNewAadhar = this.officerRepository.findByAadharNumber(updatedOfficer.getAadharNumber());
	        if (existingOfficerWithNewAadhar != null && !existingOfficerWithNewAadhar.getId().equals(id))
	            throw new IllegalArgumentException("An Transportor with the same Aadhar number already exists"); 
	        officerToUpdate.setAadharNumber(updatedOfficer.getAadharNumber());
	        officerToUpdate.setFullName(updatedOfficer.getFullName());
	        officerToUpdate.setMobileNumber(updatedOfficer.getMobileNumber());
	        // Add trim and remove spaces from address
	        String addressWithoutSpaces = updatedOfficer.getAddress().replaceAll("\\s", "");
	        officerToUpdate.setAddress(addressWithoutSpaces);
	        return (Officer)this.officerRepository.save(officerToUpdate);
	    } 
	    throw new IllegalArgumentException("Officer not found");
	}

	public Officer addOfficer(Officer officer) {
	    Officer existingOfficer = this.officerRepository.findByAadharNumber(officer.getAadharNumber());
	    if (existingOfficer != null)
	        throw new IllegalArgumentException("An Transportor with the same Aadhar number already exists"); 
	    // Add trim and remove spaces from address before saving
	    String addressWithoutSpaces = officer.getAddress().replaceAll("\\s", "");
	    officer.setAddress(addressWithoutSpaces);
	    return (Officer)this.officerRepository.save(officer);
	}
  
  public Officer deleteOfficerDetails(Long id) {
    Optional<Officer> existingOfficer = this.officerRepository.findById(id);
    if (existingOfficer.isPresent()) {
      Officer officerToUpdate = existingOfficer.get();
      officerToUpdate.setAadharNumber(null);
      officerToUpdate.setFullName(null);
      officerToUpdate.setMobileNumber(null);
      officerToUpdate.setAddress(null);
      return (Officer)this.officerRepository.save(officerToUpdate);
    } 
    throw new IllegalArgumentException("Officer not found");
  }
  
  public String processAndSaveDetails(Long officerId) {
    if (this.officerRepository == null || this.inscanService == null)
      throw new IllegalStateException("Officer repository or Inscan service not initialized"); 
    Optional<Officer> optionalOfficer = this.officerRepository.findById(officerId);
    if (optionalOfficer.isPresent()) {
      Officer officer = optionalOfficer.get();
      StringBuilder detailsBuilder = (new StringBuilder("OFC/HPNSK/")).append(officer.getId());
      String department = "Operation";
      String sub_department = "OFC";
      String details = "OFC/HPNSK/";
      Long ofcid = officer.getId();
      String name = officer.getFullName();
      String aadharNumber = officer.getAadharNumber();
      String mobile = officer.getMobileNumber();
      String address = officer.getAddress();
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
    return "Officer not found";
  }
  
  public Officer getDetailsByAadharNumber(String aadharNumber) {
    return this.officerRepository.findByAadharNumber(aadharNumber);
  }
  
  public Officer getOperatorByAadharNumber(String aadharNumber) {
    return this.officerRepository.findByAadharNumber(aadharNumber);
  }
  
  public String getFullName(Long entityId) {
    Officer officer = this.officerRepository.findById(entityId).orElse(null);
    if (officer != null) {
      String fullName = officer.getFullName();
      return fullName;
    } 
    return "Unknown Officer";
  }
  
  public String processAndSaveLicenseGateDetails(Long officerId, RedirectAttributes redirectAttributes) {
    if (this.officerRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Officer repository or LicenseGateService service not initialized"); 
    Optional<Officer> optionalOfficer = this.officerRepository.findById(officerId);
    if (optionalOfficer.isPresent()) {
      Officer officer = optionalOfficer.get();
      StringBuilder detailsBuilder = (new StringBuilder("OFC/HPNSK/")).append(officer.getId());
      String department = "Operation";
      String sub_department = "OFC";
      String details = detailsBuilder.toString();
      Long ofcid = officer.getId();
      String name = officer.getFullName();
      String aadharNumber = officer.getAadharNumber();
      String mobile = officer.getMobileNumber();
      String address = officer.getAddress();
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
    throw new IllegalArgumentException("Officer not found");
  }
}
