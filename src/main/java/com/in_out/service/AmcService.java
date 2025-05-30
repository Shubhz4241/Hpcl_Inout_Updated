package com.in_out.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in_out.model.Amc;
import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.repo.AmcRepository;

@Service
public class AmcService {
	@Autowired
	private AmcRepository amcRepository;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private LicenseGateService licenseGateService;

	public List<Amc> addAmc(List<Integer> amcData) {
		List<Amc> amc = amcData.stream().map(intValue -> new Amc()).toList();
		return this.amcRepository.saveAll(amc);
	}

	public List<Amc> getAllAmcDetails() {
		return this.amcRepository.findAll();
	}

	public Optional<Amc> getAmcById(Long id) {
		return this.amcRepository.findById(id);
	}

	public Amc addAmc(Amc amc) {
		Amc existingAmc = this.amcRepository.findByAadharNumber(amc.getAadharNumber());
		if (existingAmc != null)
			throw new IllegalArgumentException("An Amc with the same Aadhar number already exists");
		return (Amc) this.amcRepository.save(amc);
	}

	public Amc updateAmc(Long id, Amc updatedAmc) {
		Optional<Amc> existingAmc = this.amcRepository.findById(id);
		if (existingAmc.isPresent()) {
			Amc amcToUpdate = existingAmc.get();
			Amc existingAmcWithNewAadhar = this.amcRepository.findByAadharNumber(updatedAmc.getAadharNumber());
			if (existingAmcWithNewAadhar != null && !existingAmcWithNewAadhar.getId().equals(id))
				throw new IllegalArgumentException("An amc with the same Aadhar number already exists");
			amcToUpdate.setAadharNumber(updatedAmc.getAadharNumber());
			amcToUpdate.setFullName(updatedAmc.getFullName());
			amcToUpdate.setMobileNumber(updatedAmc.getMobileNumber());
			amcToUpdate.setAddress(updatedAmc.getAddress());
			amcToUpdate.setFirmName(updatedAmc.getFirmName());
			return (Amc) this.amcRepository.save(amcToUpdate);
		}
		throw new IllegalArgumentException("Workman not found");
	}

	public Amc deleteAmcDetails(Long id) {
		Optional<Amc> existingAmc = this.amcRepository.findById(id);
		if (existingAmc.isPresent()) {
			Amc amcToUpdate = existingAmc.get();
			amcToUpdate.setAadharNumber(null);
			amcToUpdate.setFullName(null);
			amcToUpdate.setMobileNumber(null);
			amcToUpdate.setAddress(null);
			amcToUpdate.setFirmName(null);
			return (Amc) this.amcRepository.save(amcToUpdate);
		}
		throw new IllegalArgumentException("Amc not found");
	}

	public String processAndSaveDetails(Long amcId) {
		if (this.amcRepository == null || this.inscanService == null)
			throw new IllegalStateException("AMC repository or Inscan service not initialized");
		Optional<Amc> optionalAmc = this.amcRepository.findById(amcId);
		if (optionalAmc.isPresent()) {
			Amc amc = optionalAmc.get();
			StringBuilder detailsBuilder = (new StringBuilder("AMC/HPNSK/")).append(amc.getId());
			String department = "Project";
			String sub_department = "AMC";
			String details = "AMC/HPNSK/";
			Long ofcid = amc.getId();
			String name = amc.getFullName();
			String aadharNumber = amc.getAadharNumber();
			String mobile = amc.getMobileNumber();
			String address = amc.getAddress();
			String contractor = null;
			String firmName = null;
			Inscan inscan = this.inscanService.findByAadhar(aadharNumber);
			if (inscan == null) {
				String str = "Y";
				this.inscanService.saveDetailsToInscan(details, name, aadharNumber, mobile, address, ofcid, department,
						sub_department, str, contractor, firmName);
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
				this.inscanService.saveDetailsToInscan(details, name, aadharNumber, mobile, address, ofcid, department,
						sub_department, mainGateStatus, contractor, firmName);
				return "Scan In";
			}
			this.inscanService.updateDetailsToInscan(inscan);
			return "Scan Out";
		}
		throw new IllegalArgumentException("AMC not found");
	}

	public Amc getDetailsByAadharNumber(String aadharNumber) {
		return this.amcRepository.findByAadharNumber(aadharNumber);
	}

	public String getFullName(Long entityId) {
		Amc amc = this.amcRepository.findById(entityId).orElse(null);
		if (amc != null) {
			String fullName = amc.getFullName();
			return fullName;
		}
		return "Unknown Amc";
	}

	public String processAndSaveLicenseGateDetails(Long amcId) {
		if (this.amcRepository == null || this.licenseGateService == null)
			throw new IllegalStateException("AMC repository or LicenseGateService service not initialized");
		Optional<Amc> optionalAMC = this.amcRepository.findById(amcId);
		if (optionalAMC.isPresent()) {
			Amc amc = optionalAMC.get();
			StringBuilder detailsBuilder = (new StringBuilder("AMC/HPNSK/")).append(amc.getId());
			String department = "Operation";
			String sub_department = "AMC";
			String details = detailsBuilder.toString();
			Long ofcId = amc.getId();
			String name = amc.getFullName();
			String aadharNumber = amc.getAadharNumber();
			String mobile = amc.getMobileNumber();
			String address = amc.getAddress();
			String contractor = null;
			String firmName = null;
			Licensegate licenseGate = this.licenseGateService.findByAadhar(aadharNumber);
			if (licenseGate == null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, amcId,
						department, sub_department, contractor, firmName);
				return "In";
			}
			if (licenseGate.getExitDateTime() != null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, amcId,
						department, sub_department, contractor, firmName);
				return "Scan In";
			}
			this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
			return "Scan Out";
		}
		throw new IllegalArgumentException("AMC not found");
	}
}
