package com.in_out.service;

import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.model.Tat;
import com.in_out.repo.TatRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TatService {
	@Autowired
	private TatRepository tatRepository;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private LicenseGateService licenseGateService;

	public List<Tat> addTat(List<Integer> tatData) {
		List<Tat> tat = tatData.stream().map(intValue -> new Tat()).toList();
		return this.tatRepository.saveAll(tat);
	}

	public List<Tat> getAllTatDetails() {
		return this.tatRepository.findAll();
	}

	public Optional<Tat> getTatById(Long id) {
		return this.tatRepository.findById(id);
	}

	public Tat addTat(Tat tat) {
		Tat existingTat = this.tatRepository.findByAadharNumber(tat.getAadharNumber());
		if (existingTat != null)
			throw new IllegalArgumentException("An Tat with the same Aadhar number already exists");
		return (Tat) this.tatRepository.save(tat);
	}

	public Tat updateTat(Long id, Tat updatedTat) {
		Optional<Tat> existingTat = this.tatRepository.findById(id);
		if (existingTat.isPresent()) {
			Tat tatToUpdate = existingTat.get();
			Tat existingTatWithNewAadhar = this.tatRepository.findByAadharNumber(updatedTat.getAadharNumber());
			if (existingTatWithNewAadhar != null && !existingTatWithNewAadhar.getId().equals(id))
				throw new IllegalArgumentException("An Tat with the same Aadhar number already exists");
			tatToUpdate.setAadharNumber(updatedTat.getAadharNumber());
			tatToUpdate.setFullName(updatedTat.getFullName());
			tatToUpdate.setMobileNumber(updatedTat.getMobileNumber());
			tatToUpdate.setAddress(updatedTat.getAddress());
			return (Tat) this.tatRepository.save(tatToUpdate);
		}
		throw new IllegalArgumentException("Tat not found");
	}

	public Tat deleteTatDetails(Long id) {
		Optional<Tat> existingTat = this.tatRepository.findById(id);
		if (existingTat.isPresent()) {
			Tat tatToUpdate = existingTat.get();
			tatToUpdate.setAadharNumber(null);
			tatToUpdate.setFullName(null);
			tatToUpdate.setMobileNumber(null);
			tatToUpdate.setAddress(null);
			return (Tat) this.tatRepository.save(tatToUpdate);
		}
		throw new IllegalArgumentException("Tat not found");
	}

	public String processAndSaveDetails(Long tatId) {
		if (this.tatRepository == null || this.inscanService == null)
			throw new IllegalStateException("Tat repository or Inscan service not initialized");
		Optional<Tat> optionalTat = this.tatRepository.findById(tatId);
		if (optionalTat.isPresent()) {
			Tat tat = optionalTat.get();
			StringBuilder detailsBuilder = (new StringBuilder("TAT/HPNSK/")).append(tat.getId());
			String department = "Operation";
			String sub_department = "TAT";
			String details = "TAT/HPNSK/";
			Long ofcid = tat.getId();
			String name = tat.getFullName();
			String aadharNumber = tat.getAadharNumber();
			String mobile = tat.getMobileNumber();
			String address = tat.getAddress();
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
		throw new IllegalArgumentException("Tat not found");
	}

	public Tat getDetailsByAadharNumber(String aadharNumber) {
		return this.tatRepository.findByAadharNumber(aadharNumber);
	}

	public String getFullName(Long entityId) {
		Tat tat = this.tatRepository.findById(entityId).orElse(null);
		if (tat != null) {
			String fullName = tat.getFullName();
			return fullName;
		}
		return "Unknown Tat";
	}

	public String processAndSaveLicenseGateDetails(Long tatId) {
		if (this.tatRepository == null || this.licenseGateService == null)
			throw new IllegalStateException("Tat repository or LicenseGateService service not initialized");
		Optional<Tat> optionalTat = this.tatRepository.findById(tatId);
		if (optionalTat.isPresent()) {
			Tat tat = optionalTat.get();
			StringBuilder detailsBuilder = (new StringBuilder("TAT/HPNSK/")).append(tat.getId());
			String department = "Operation";
			String sub_department = "TAT";
			String details = detailsBuilder.toString();
			Long ofcId = tat.getId();
			String name = tat.getFullName();
			String aadharNumber = tat.getAadharNumber();
			String mobile = tat.getMobileNumber();
			String address = tat.getAddress();
			String contractor = null;
			String firmName = null;

			Licensegate licenseGate = this.licenseGateService.findByAadhar(aadharNumber);
			if (licenseGate == null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, tatId,
						department, sub_department, contractor, firmName);
				return "In";
			}
			if (licenseGate.getExitDateTime() != null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, tatId,
						department, sub_department, contractor, firmName);
				return "Scan In";
			}
			this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
			return "Scan Out";
		}
		throw new IllegalArgumentException("TAT not found");
	}
}
