package com.in_out.service;

import com.in_out.model.Feg;
import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.repo.FegRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FegService {
	@Autowired
	private FegRepository fegRepository;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private LicenseGateService licenseGateService;

	/*
	 * public List<Feg> addFeg(List<Integer> fegData) { List<Feg> feg =
	 * fegData.stream().map(intValue -> new Feg()).toList(); return
	 * this.fegRepository.saveAll(feg); }
	 */
	public List<Feg> addFeg(List<Integer> fegData) {
		if (fegData.stream().anyMatch(i -> i <= 0)) {
			throw new IllegalArgumentException("Feg data cannot contain negative or zero values");
		}
		List<Feg> feg = fegData.stream().map(intValue -> new Feg()).toList();
		return this.fegRepository.saveAll(feg);
	}

	public List<Feg> getAllFegDetails() {
		return this.fegRepository.findAll();
	}

	public Optional<Feg> getFegById(Long id) {
		return this.fegRepository.findById(id);
	}

	public Feg addFeg(Feg feg) {
		Feg existingFeg = this.fegRepository.findByAadharNumber(feg.getAadharNumber());
		if (existingFeg != null)
			throw new IllegalArgumentException("An Feg with the same Aadhar number already exists");
		return (Feg) this.fegRepository.save(feg);
	}

	public Feg updateFeg(Long id, Feg updatedFeg) {
		Optional<Feg> existingFeg = this.fegRepository.findById(id);
		if (existingFeg.isPresent()) {
			Feg fegToUpdate = existingFeg.get();
			Feg existingFegWithNewAadhar = this.fegRepository.findByAadharNumber(updatedFeg.getAadharNumber());
			if (existingFegWithNewAadhar != null && !existingFegWithNewAadhar.getId().equals(id))
				throw new IllegalArgumentException("An Feg with the same Aadhar number already exists");
			fegToUpdate.setAadharNumber(updatedFeg.getAadharNumber());
			fegToUpdate.setFullName(updatedFeg.getFullName());
			fegToUpdate.setMobileNumber(updatedFeg.getMobileNumber());
			fegToUpdate.setAddress(updatedFeg.getAddress());
			return (Feg) this.fegRepository.save(fegToUpdate);
		}
		throw new IllegalArgumentException("Feg not found");
	}

	public Feg deleteFegDetails(Long id) {
		Optional<Feg> existingFeg = this.fegRepository.findById(id);
		if (existingFeg.isPresent()) {
			Feg fegToUpdate = existingFeg.get();
			fegToUpdate.setAadharNumber(null);
			fegToUpdate.setFullName(null);
			fegToUpdate.setMobileNumber(null);
			fegToUpdate.setAddress(null);
			return (Feg) this.fegRepository.save(fegToUpdate);
		}
		throw new IllegalArgumentException("Feg not found");
	}

	public String processAndSaveDetails(Long fegId) {
		if (this.fegRepository == null || this.inscanService == null)
			throw new IllegalStateException("Feg repository or Inscan service not initialized");
		Optional<Feg> optionalFeg = this.fegRepository.findById(fegId);
		if (optionalFeg.isPresent()) {
			Feg feg = optionalFeg.get();
			StringBuilder detailsBuilder = (new StringBuilder("FEG/HPNSK/")).append(feg.getId());
			String department = "Operation";
			String sub_department = "FEG";
			String details = "FEG/HPNSK/";
			Long ofcid = feg.getId();
			String name = feg.getFullName();
			String aadharNumber = feg.getAadharNumber();
			String mobile = feg.getMobileNumber();
			String address = feg.getAddress();
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
		throw new IllegalArgumentException("Feg not found");
	}

	public Feg getDetailsByAadharNumber(String aadharNumber) {
		return this.fegRepository.findByAadharNumber(aadharNumber);
	}

	public String getFullName(Long entityId) {
		Feg feg = this.fegRepository.findById(entityId).orElse(null);
		if (feg != null) {
			String fullName = feg.getFullName();
			return fullName;
		}
		return "Unknown Feg";
	}

	public String processAndSaveLicenseGateDetails(Long fegId) {
		if (this.fegRepository == null || this.licenseGateService == null)
			throw new IllegalStateException("Feg repository or LicenseGateService service not initialized");
		Optional<Feg> optionalFeg = this.fegRepository.findById(fegId);
		if (optionalFeg.isPresent()) {
			Feg feg = optionalFeg.get();
			StringBuilder detailsBuilder = (new StringBuilder("FEG/HPNSK/")).append(feg.getId());
			String department = "Operation";
			String sub_department = "FEG";
			String details = detailsBuilder.toString();
			Long ofcId = feg.getId();
			String name = feg.getFullName();
			String aadharNumber = feg.getAadharNumber();
			String mobile = feg.getMobileNumber();
			String address = feg.getAddress();
			String contractor = null;
			String firmName = null;

			Licensegate licenseGate = this.licenseGateService.findByAadhar(aadharNumber);
			if (licenseGate == null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, fegId,
						department, sub_department, contractor, firmName);
				return "In";
			}
			if (licenseGate.getExitDateTime() != null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, fegId,
						department, sub_department, contractor, firmName);
				return "Scan In";
			}
			this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
			return "Scan Out";
		}
		throw new IllegalArgumentException("Feg not found");
	}
}
