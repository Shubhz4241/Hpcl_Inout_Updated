package com.in_out.service;


import com.in_out.model.Contractorworkman;
import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.repo.ContractorworkmanRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractorworkmanService {
  @Autowired
  private ContractorworkmanRepository contractorworkmanRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
  public List<Contractorworkman> addContractorworkman(List<Integer> contractorworkmanData) {
    List<Contractorworkman> contractorworkman = contractorworkmanData.stream().map(intValue -> new Contractorworkman()).toList();
    return this.contractorworkmanRepository.saveAll(contractorworkman);
  }
  
  public List<Contractorworkman> getAllContractorworkmanDetails() {
    return this.contractorworkmanRepository.findAll();
  }
  
  public Optional<Contractorworkman> getContractorworkmanById(Long id) {
    return this.contractorworkmanRepository.findById(id);
  }
  
  public Contractorworkman addContractorworkman(Contractorworkman contractorworkman) {
    Contractorworkman existingContractorworkman = this.contractorworkmanRepository.findByAadharNumber(contractorworkman.getAadharNumber());
    if (existingContractorworkman != null)
      throw new IllegalArgumentException("An Contractorworkman with the same Aadhar number already exists"); 
    return (Contractorworkman)this.contractorworkmanRepository.save(contractorworkman);
  }
  
  public Contractorworkman updateContractorworkman(Long id, Contractorworkman updatedContractorworkman) {
    Optional<Contractorworkman> existingContractorworkman = this.contractorworkmanRepository.findById(id);
    if (existingContractorworkman.isPresent()) {
      Contractorworkman contractorworkmanToUpdate = existingContractorworkman.get();
      Contractorworkman existingContractorworkmanWithNewAadhar = this.contractorworkmanRepository.findByAadharNumber(updatedContractorworkman.getAadharNumber());
      if (existingContractorworkmanWithNewAadhar != null && !existingContractorworkmanWithNewAadhar.getId().equals(id))
        throw new IllegalArgumentException("An Contractorworkman with the same Aadhar number already exists"); 
      contractorworkmanToUpdate.setAadharNumber(updatedContractorworkman.getAadharNumber());
      contractorworkmanToUpdate.setFullName(updatedContractorworkman.getFullName());
      contractorworkmanToUpdate.setMobileNumber(updatedContractorworkman.getMobileNumber());
      contractorworkmanToUpdate.setAddress(updatedContractorworkman.getAddress());
      contractorworkmanToUpdate.setFirmName(updatedContractorworkman.getFirmName());
      contractorworkmanToUpdate.setContractor(updatedContractorworkman.getContractor());

      return (Contractorworkman)this.contractorworkmanRepository.save(contractorworkmanToUpdate);
    } 
    throw new IllegalArgumentException("Contractor not found");
  }
  
  public Contractorworkman deleteContractorworkmanDetails(Long id) {
    Optional<Contractorworkman> existingContractorworkman = this.contractorworkmanRepository.findById(id);
    if (existingContractorworkman.isPresent()) {
      Contractorworkman contractorworkmanToUpdate = existingContractorworkman.get();
      contractorworkmanToUpdate.setAadharNumber(null);
      contractorworkmanToUpdate.setFullName(null);
      contractorworkmanToUpdate.setMobileNumber(null);
      contractorworkmanToUpdate.setAddress(null);
      contractorworkmanToUpdate.setFirmName(null);
      contractorworkmanToUpdate.setContractor(null);

      return (Contractorworkman)this.contractorworkmanRepository.save(contractorworkmanToUpdate);
    } 
    throw new IllegalArgumentException("Contractorworkman not found");
  }
  
  public String processAndSaveDetails(Long contractorworkmanId) {
    if (this.contractorworkmanRepository == null || this.inscanService == null)
      throw new IllegalStateException("Contractorworkman repository or Inscan service not initialized"); 
    Optional<Contractorworkman> optionalContractorworkman = this.contractorworkmanRepository.findById(contractorworkmanId);
    if (optionalContractorworkman.isPresent()) {
      Contractorworkman contractorworkman = optionalContractorworkman.get();
      StringBuilder detailsBuilder = (new StringBuilder("CONW/HPNSK/")).append(contractorworkman.getId());
      String department = "Operation";
      String sub_department = "CONW";
      String details = "CONW/HPNSK/";
      Long ofcid = contractorworkman.getId();
      String name = contractorworkman.getFullName();
      String aadharNumber = contractorworkman.getAadharNumber();
      String mobile = contractorworkman.getMobileNumber();
      String address = contractorworkman.getAddress();
      String contractor=contractorworkman.getContractor();
      String firmName=contractorworkman.getFirmName();
      
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
    throw new IllegalArgumentException("Contractorworkman not found");
  }
  
  public Contractorworkman getDetailsByAadharNumber(String aadharNumber) {
    return this.contractorworkmanRepository.findByAadharNumber(aadharNumber);
  }
  
  public String getFullName(Long entityId) {
    Contractorworkman contractorworkman = this.contractorworkmanRepository.findById(entityId).orElse(null);
    if (contractorworkman != null) {
      String fullName = contractorworkman.getFullName();
      return fullName;
    } 
    return "Unknown Contractorworkman";
  }
  
  public String processAndSaveLicenseGateDetails(Long contractorWorkmanId) {
    if (this.contractorworkmanRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("ContractorWorkman repository or LicenseGateService service not initialized"); 
    Optional<Contractorworkman> optionalContractorWorkman = this.contractorworkmanRepository.findById(contractorWorkmanId);
    if (optionalContractorWorkman.isPresent()) {
      Contractorworkman contractorWorkman = optionalContractorWorkman.get();
      StringBuilder detailsBuilder = (new StringBuilder("CONW/HPNSK/")).append(contractorWorkman.getId());
      String department = "Operation";
      String sub_department = "CONW";
      String details = detailsBuilder.toString();
      Long ofcId = contractorWorkman.getId();
      String name = contractorWorkman.getFullName();
      String aadharNumber = contractorWorkman.getAadharNumber();
      String mobile = contractorWorkman.getMobileNumber();
      String address = contractorWorkman.getAddress();
      String contractor=contractorWorkman.getContractor();
      String firmName=contractorWorkman.getFirmName();
       
      Licensegate licenseGate = this.licenseGateService.findByAadhar(aadharNumber);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, contractorWorkmanId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, contractorWorkmanId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("ContractorWorkman not found");
  }
}