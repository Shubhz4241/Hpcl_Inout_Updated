package com.in_out.service;


import com.in_out.model.Bulk;
import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.repo.BulkRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BulkService {
  @Autowired
  private BulkRepository bulkRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
	/*
	 * public List<Bulk> addBulk(List<Integer> bulkData) { List<Bulk> bulk =
	 * bulkData.stream().map(intValue -> new Bulk()).toList(); return
	 * this.bulkRepository.saveAll(bulk); }
	 */
  public List<Bulk> addBulk(List<Integer> bulkData) {
	    if (bulkData.stream().anyMatch(i -> i <= 0)) {
	        throw new IllegalArgumentException("Bulk data cannot contain negative or zero values"); // Fixed Defect 1
	    }
	    List<Bulk> bulk = bulkData.stream().map(intValue -> new Bulk()).toList();
	    return this.bulkRepository.saveAll(bulk);
	}
  
  public List<Bulk> getAllBulkDetails() {
    return this.bulkRepository.findAll();
  }
  
  public Optional<Bulk> getBulkById(Long id) {
    return this.bulkRepository.findById(id);
  }
  
  public Bulk addBulk(Bulk bulk) {
      Bulk existingBulkByAadhar = this.bulkRepository.findByAadharNumber(bulk.getAadharNumber());
      if (existingBulkByAadhar != null)
          throw new IllegalArgumentException("An Bulk with the same Aadhar number already exists");
      // Check for duplicate truck number
      if (bulk.getTruckNumber() != null && !bulk.getTruckNumber().isEmpty()) {
          Bulk existingBulkByTruck = this.bulkRepository.findByTruckNumber(bulk.getTruckNumber());
          if (existingBulkByTruck != null)
              throw new IllegalArgumentException("An Bulk with the same Truck Number already exists");
      }
      return (Bulk) this.bulkRepository.save(bulk);
  }

  public Bulk updateBulk(Long id, Bulk updatedBulk) {
      Optional<Bulk> existingBulk = this.bulkRepository.findById(id);
      if (existingBulk.isPresent()) {
          Bulk bulkToUpdate = existingBulk.get();
          Bulk existingBulkWithNewAadhar = this.bulkRepository
                  .findByAadharNumber(updatedBulk.getAadharNumber());
          if (existingBulkWithNewAadhar != null && !existingBulkWithNewAadhar.getId().equals(id))
              throw new IllegalArgumentException("An Bulk with the same Aadhar number already exists");
          // Check for duplicate truck number
          if (updatedBulk.getTruckNumber() != null && !updatedBulk.getTruckNumber().isEmpty()) {
              Bulk existingBulkByTruck = this.bulkRepository.findByTruckNumber(updatedBulk.getTruckNumber());
              if (existingBulkByTruck != null && !existingBulkByTruck.getId().equals(id))
                  throw new IllegalArgumentException("An Bulk with the same Truck Number already exists");
          }
          bulkToUpdate.setAadharNumber(updatedBulk.getAadharNumber());
          bulkToUpdate.setFullName(updatedBulk.getFullName());
          bulkToUpdate.setMobileNumber(updatedBulk.getMobileNumber());
          bulkToUpdate.setAddress(updatedBulk.getAddress());
          bulkToUpdate.setFirmName(updatedBulk.getFirmName());
          bulkToUpdate.setTruckNumber(updatedBulk.getTruckNumber());
          return (Bulk) this.bulkRepository.save(bulkToUpdate);
      }
      throw new IllegalArgumentException("Bulk not found");
  }
  
  public Bulk deleteBulkDetails(Long id) {
    Optional<Bulk> existingBulk = this.bulkRepository.findById(id);
    if (existingBulk.isPresent()) {
      Bulk bulkToUpdate = existingBulk.get();
      bulkToUpdate.setAadharNumber(null);
      bulkToUpdate.setFullName(null);
      bulkToUpdate.setMobileNumber(null);
      bulkToUpdate.setAddress(null);
      bulkToUpdate.setFirmName(null);
      return (Bulk)this.bulkRepository.save(bulkToUpdate);
    } 
    throw new IllegalArgumentException("Bulk not found");
  }
  
  public String processAndSaveDetails(Long bulkId) {
    if (this.bulkRepository == null || this.inscanService == null)
      throw new IllegalStateException("Bulk repository or Inscan service not initialized"); 
    Optional<Bulk> optionalBulk = this.bulkRepository.findById(bulkId);
    if (optionalBulk.isPresent()) {
      Bulk bulk = optionalBulk.get();
      StringBuilder detailsBuilder = (new StringBuilder("BK/HPNSK/")).append(bulk.getId());
      String department = "Driver";
      String sub_department = "BK";
      String details = "BK/HPNSK/";
      Long ofcid = bulk.getId();
      String name = bulk.getFullName();
      String aadharNumber = bulk.getAadharNumber();
      String mobile = bulk.getMobileNumber();
      String address = bulk.getAddress();
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
    throw new IllegalArgumentException("Bulk not found");
  }
  
  public Bulk getDetailsByAadharNumber(String aadharNumber) {
    return this.bulkRepository.findByAadharNumber(aadharNumber);
  }
  
  public String getFullName(Long entityId) {
    Bulk bulk = this.bulkRepository.findById(entityId).orElse(null);
    if (bulk != null) {
      String fullName = bulk.getFullName();
      return fullName;
    } 
    return "Unknown Bulk";
  }
  
  public String processAndSaveLicenseGateDetails(Long bulkId) {
    if (this.bulkRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("Bulk repository or LicenseGateService service not initialized"); 
    Optional<Bulk> optionalBulk = this.bulkRepository.findById(bulkId);
    if (optionalBulk.isPresent()) {
      Bulk bulk = optionalBulk.get();
      StringBuilder detailsBuilder = (new StringBuilder("BK/HPNSK/")).append(bulk.getId());
      String department = "Operation";
      String sub_department = "BK";
      String details = detailsBuilder.toString();
      Long ofcId = bulk.getId();
      String name = bulk.getFullName();
      String aadharNumber = bulk.getAadharNumber();
      String mobile = bulk.getMobileNumber();
      String address = bulk.getAddress();
      String contractor=null;
      String firmName=null;
      
      Licensegate licenseGate = this.licenseGateService.findByAadhar(aadharNumber);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, bulkId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, bulkId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("Bulk not found");
  }
}
