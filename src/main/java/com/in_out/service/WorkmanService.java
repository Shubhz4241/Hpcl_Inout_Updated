package com.in_out.service;

import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.model.Workman;
import com.in_out.repo.WorkmanRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkmanService {
	@Autowired
	private WorkmanRepository workmanRepository;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private LicenseGateService licenseGateService;

	/*
	 * public List<Workman> addWorkman(List<Integer> workmanData) { List<Workman>
	 * workmans = workmanData.stream().map(intValue -> new Workman()).toList();
	 * return this.workmanRepository.saveAll(workmans); }
	 */
	public List<Workman> addWorkman(List<Integer> workmanData) {
		if (workmanData.stream().anyMatch(i -> i <= 0)) {
			throw new IllegalArgumentException("Workman data cannot contain negative or zero values"); // Fixed Defect 1
		}
		List<Workman> workmans = workmanData.stream().map(intValue -> new Workman()).toList();
		return this.workmanRepository.saveAll(workmans);
	}

	public List<Workman> getAllWorkmanDetails() {
		return this.workmanRepository.findAll();
	}

	public Optional<Workman> getWorkmanById(Long id) {
		return this.workmanRepository.findById(id);
	}

	public Workman addWorkman(Workman workman) {
		Workman existingWorkman = this.workmanRepository.findByAadharNumber(workman.getAadharNumber());
		if (existingWorkman != null)
			throw new IllegalArgumentException("An Workman with the same Aadhar number already exists");
		return (Workman) this.workmanRepository.save(workman);
	}

	public Workman updateWorkman(Long id, Workman updatedWorkman) {
		Optional<Workman> existingWorkman = this.workmanRepository.findById(id);
		if (existingWorkman.isPresent()) {
			Workman workmanToUpdate = existingWorkman.get();
			Workman existingWorkmanWithNewAadhar = this.workmanRepository
					.findByAadharNumber(updatedWorkman.getAadharNumber());
			if (existingWorkmanWithNewAadhar != null && !existingWorkmanWithNewAadhar.getId().equals(id))
				throw new IllegalArgumentException("An Workman with the same Aadhar number already exists");
			workmanToUpdate.setAadharNumber(updatedWorkman.getAadharNumber());
			workmanToUpdate.setFullName(updatedWorkman.getFullName());
			workmanToUpdate.setMobileNumber(updatedWorkman.getMobileNumber());
			workmanToUpdate.setAddress(updatedWorkman.getAddress());
			workmanToUpdate.setFirmName(updatedWorkman.getFirmName());
			return (Workman) this.workmanRepository.save(workmanToUpdate);
		}
		throw new IllegalArgumentException("Workman not found");
	}

	public Workman deleteWorkmanDetails(Long id) {
		Optional<Workman> existingWorkman = this.workmanRepository.findById(id);
		if (existingWorkman.isPresent()) {
			Workman workmanToUpdate = existingWorkman.get();
			workmanToUpdate.setAadharNumber(null);
			workmanToUpdate.setFullName(null);
			workmanToUpdate.setMobileNumber(null);
			workmanToUpdate.setAddress(null);
			return (Workman) this.workmanRepository.save(workmanToUpdate);
		}
		throw new IllegalArgumentException("Workman not found");
	}

	public String processAndSaveDetails(Long workmanId) {
		if (this.workmanRepository == null || this.inscanService == null)
			throw new IllegalStateException("Workman repository or Inscan service not initialized");
		Optional<Workman> optionalWorkman = this.workmanRepository.findById(workmanId);
		if (optionalWorkman.isPresent()) {
			Workman workman = optionalWorkman.get();
			StringBuilder detailsBuilder = (new StringBuilder("PW/HPNSK/")).append(workman.getId());
			String department = "Project";
			String sub_department = "PW";
			String details = "PW/HPNSK/";
			Long ofcid = workman.getId();
			String name = workman.getFullName();
			String aadharNumber = workman.getAadharNumber();
			String mobile = workman.getMobileNumber();
			String address = workman.getAddress();
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
		throw new IllegalArgumentException("Workman not found");
	}

	public Workman getDetailsByAadharNumber(String aadharNumber) {
		return this.workmanRepository.findByAadharNumber(aadharNumber);
	}

	public String getFullName(Long entityId) {
		Workman workman = this.workmanRepository.findById(entityId).orElse(null);
		if (workman != null) {
			String fullName = workman.getFullName();
			return fullName;
		}
		return "Unknown Workman";
	}

	public String processAndSaveLicenseGateDetails(Long workmanId) {
		if (this.workmanRepository == null || this.licenseGateService == null)
			throw new IllegalStateException("Workman repository or LicenseGateService service not initialized");
		Optional<Workman> optionalWorkman = this.workmanRepository.findById(workmanId);
		if (optionalWorkman.isPresent()) {
			Workman workman = optionalWorkman.get();
			StringBuilder detailsBuilder = (new StringBuilder("PW/HPNSK/")).append(workman.getId());
			String department = "Operation";
			String sub_department = "PW";
			String details = detailsBuilder.toString();
			Long ofcId = workman.getId();
			String name = workman.getFullName();
			String aadharNumber = workman.getAadharNumber();
			String mobile = workman.getMobileNumber();
			String address = workman.getAddress();
			String contractor = null;
			String firmName = null;

			Licensegate licenseGate = this.licenseGateService.findByAadhar(aadharNumber);
			if (licenseGate == null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address,
						workmanId, department, sub_department, contractor, firmName);
				return "In";
			}
			if (licenseGate.getExitDateTime() != null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address,
						workmanId, department, sub_department, contractor, firmName);
				return "Scan In";
			}
			this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
			return "Scan Out";
		}
		throw new IllegalArgumentException("Workman not found");
	}
}
