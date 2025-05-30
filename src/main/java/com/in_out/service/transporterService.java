package com.in_out.service;


import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.model.transporter;
import com.in_out.repo.TransporterRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class transporterService {
  @Autowired
  private TransporterRepository transporterRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licenseGateService;
  
	/*
	 * public List<transporter> addtransporter(List<Integer> transporterData) {
	 * List<transporter> transporter = transporterData.stream().map(intValue -> new
	 * transporter()).toList(); return
	 * this.transporterRepository.saveAll(transporter); }
	 */
  
  public List<transporter> addtransporter(List<Integer> transporterData) {
	    if (transporterData.stream().anyMatch(i -> i <= 0)) {
	        throw new IllegalArgumentException("Transporter data cannot contain negative or zero values"); // Fixed Defect 1
	    }
	    List<transporter> transporter = transporterData.stream().map(intValue -> new transporter()).toList();
	    return this.transporterRepository.saveAll(transporter);
	}
  
  public List<transporter> getAlltransporterDetails() {
    return this.transporterRepository.findAll();
  }
  
  public Optional<transporter> gettransporterById(Long id) {
    return this.transporterRepository.findById(id);
  }
  
  public transporter addtransporter(transporter transporter) {
	    transporter existingtransporter = this.transporterRepository.findByAadharNumber(transporter.getAadharNumber());
	    if (existingtransporter != null) {
	        throw new IllegalArgumentException("An transporter with the same Aadhar number already exists");
	    }
	    if (transporter.getTruckNumber() != null && !transporter.getTruckNumber().isEmpty()) {
	        transporter existingByTruck = this.transporterRepository.findByTruckNumber(transporter.getTruckNumber());
	        if (existingByTruck != null) {
	            throw new IllegalArgumentException("A transporter with the same truck number already exists");
	        }
	    }
	    return this.transporterRepository.save(transporter);
	}
  
  public transporter updatetransporter(Long id, transporter updatedtransporter) {
	    Optional<transporter> existingtransporter = this.transporterRepository.findById(id);
	    if (existingtransporter.isPresent()) {
	        transporter transporterToUpdate = existingtransporter.get();
	        transporter existingtransporterWithNewAadhar = this.transporterRepository.findByAadharNumber(updatedtransporter.getAadharNumber());
	        if (existingtransporterWithNewAadhar != null && !existingtransporterWithNewAadhar.getId().equals(id)) {
	            throw new IllegalArgumentException("An transporter with the same Aadhar number already exists");
	        }
	        if (updatedtransporter.getTruckNumber() != null && !updatedtransporter.getTruckNumber().isEmpty()) {
	            transporter existingByTruck = this.transporterRepository.findByTruckNumber(updatedtransporter.getTruckNumber());
	            if (existingByTruck != null && !existingByTruck.getId().equals(id)) {
	                throw new IllegalArgumentException("A transporter with the same truck number already exists");
	            }
	        }
	        transporterToUpdate.setAadharNumber(updatedtransporter.getAadharNumber());
	        transporterToUpdate.setFullName(updatedtransporter.getFullName());
	        transporterToUpdate.setMobileNumber(updatedtransporter.getMobileNumber());
	        transporterToUpdate.setAddress(updatedtransporter.getAddress());
	        transporterToUpdate.setTruckNumber(updatedtransporter.getTruckNumber());
	        return this.transporterRepository.save(transporterToUpdate);
	    }
	    throw new IllegalArgumentException("transporter not found");
	}
  
  public transporter deletetransporterDetails(Long id) {
    Optional<transporter> existingtransporter = this.transporterRepository.findById(id);
    if (existingtransporter.isPresent()) {
      transporter transporterToUpdate = existingtransporter.get();
      transporterToUpdate.setAadharNumber(null);
      transporterToUpdate.setFullName(null);
      transporterToUpdate.setMobileNumber(null);
      transporterToUpdate.setAddress(null);
      return (transporter)this.transporterRepository.save(transporterToUpdate);
    } 
    throw new IllegalArgumentException("transporter not found");
  }
  
  public String processAndSaveDetails(Long transporterId) {
    if (this.transporterRepository == null || this.inscanService == null)
      throw new IllegalStateException("transporter repository or Inscan service not initialized"); 
    Optional<transporter> optionaltransporter = this.transporterRepository.findById(transporterId);
    if (optionaltransporter.isPresent()) {
      transporter transporter = optionaltransporter.get();
      StringBuilder detailsBuilder = (new StringBuilder("TR/HPNSK/")).append(transporter.getId());
      String department = "Driver";
      String sub_department = "TR";
      String details = "TR/HPNSK/";
      Long ofcid = transporter.getId();
      String name = transporter.getFullName();
      String aadharNumber = transporter.getAadharNumber();
      String mobile = transporter.getMobileNumber();
      String address = transporter.getAddress();
      String contractor="null";
      String firmName="null";
      
      
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
    throw new IllegalArgumentException("transporter not found");
  }
  
  public transporter getDetailsByAadharNumber(String aadharNumber) {
    return this.transporterRepository.findByAadharNumber(aadharNumber);
  }
  
  public String getFullName(Long entityId) {
    transporter transporter = this.transporterRepository.findById(entityId).orElse(null);
    if (transporter != null) {
      String fullName = transporter.getFullName();
      return fullName;
    } 
    return "Unknown transporter";
  }
  
  public String processAndSaveLicenseGateDetails(Long transporterId) {
    if (this.transporterRepository == null || this.licenseGateService == null)
      throw new IllegalStateException("transporter repository or LicenseGateService service not initialized"); 
    Optional<transporter> optionaltransporter = this.transporterRepository.findById(transporterId);
    if (optionaltransporter.isPresent()) {
      transporter transporter = optionaltransporter.get();
      StringBuilder detailsBuilder = (new StringBuilder("TR/HPNSK/")).append(transporter.getId());
      String department = "Operation";
      String sub_department = "TR";
      String details = detailsBuilder.toString();
      Long ofcId = transporter.getId();
      String name = transporter.getFullName();
      String aadharNumber = transporter.getAadharNumber();
      String mobile = transporter.getMobileNumber();
      String address = transporter.getAddress();
      String contractor="null";
      String firmName="null";
      
      
      Licensegate licenseGate = this.licenseGateService.findByAadhar(aadharNumber);
      if (licenseGate == null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, transporterId, department, sub_department,contractor,firmName);
        return "In";
      } 
      if (licenseGate.getExitDateTime() != null) {
        this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, transporterId, department, sub_department,contractor,firmName);
        return "Scan In";
      } 
      this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
      return "Scan Out";
    } 
    throw new IllegalArgumentException("transporter not found");
  }
}
