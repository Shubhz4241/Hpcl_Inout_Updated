package com.in_out.service;

import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.model.Packed;
import com.in_out.repo.PackedRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackedService {
	@Autowired
	private PackedRepository packedRepository;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private LicenseGateService licenseGateService;

	/*
	 * public List<Packed> addPacked(List<Integer> PackedData) { List<Packed> packed
	 * = PackedData.stream().map(intValue -> new Packed()).toList(); return
	 * this.packedRepository.saveAll(packed); }
	 */
	public List<Packed> addPacked(List<Integer> packedData) {
		if (packedData.stream().anyMatch(i -> i <= 0)) {
			throw new IllegalArgumentException("Packed data cannot contain negative or zero values"); // Fixed Defect 1
		}
		List<Packed> packed = packedData.stream().map(intValue -> new Packed()).toList();
		return this.packedRepository.saveAll(packed);
	}

	public List<Packed> getAllPackedDetails() {
		return this.packedRepository.findAll();
	}

	public Optional<Packed> getPackedById(Long id) {
		return this.packedRepository.findById(id);
	}

	public Packed addPacked(Packed packed) {
		Packed existingPacked = this.packedRepository.findByAadharNumber(packed.getAadharNumber());
		if (existingPacked != null)
			throw new IllegalArgumentException("An Packed with the same Aadhar number already exists");
		String truckNumber = packed.getTruckNumber();
		if (truckNumber != null && !truckNumber.trim().isEmpty()) {
			Packed existingTruck = this.packedRepository.findByTruckNumber(truckNumber.trim());
			if (existingTruck != null)
				throw new IllegalArgumentException("A Packed with the same truck number already exists");
			packed.setTruckNumber(truckNumber.trim());
		}
		return this.packedRepository.save(packed);
	}

	public Packed updatePacked(Long id, Packed updatedPacked) {
		Optional<Packed> existingPacked = this.packedRepository.findById(id);
		if (existingPacked.isPresent()) {
			Packed packedToUpdate = existingPacked.get();
			Packed existingPackedWithNewAadhar = this.packedRepository
					.findByAadharNumber(updatedPacked.getAadharNumber());
			if (existingPackedWithNewAadhar != null && !existingPackedWithNewAadhar.getId().equals(id))
				throw new IllegalArgumentException("An Packed with the same Aadhar number already exists");
			String truckNumber = updatedPacked.getTruckNumber();
			if (truckNumber != null && !truckNumber.trim().isEmpty()) {
				Packed existingTruck = this.packedRepository.findByTruckNumber(truckNumber.trim());
				if (existingTruck != null && !existingTruck.getId().equals(id))
					throw new IllegalArgumentException("A Packed with the same truck number already exists");
				packedToUpdate.setTruckNumber(truckNumber.trim());
			} else {
				packedToUpdate.setTruckNumber(null);
			}
			packedToUpdate.setAadharNumber(updatedPacked.getAadharNumber());
			packedToUpdate.setFullName(updatedPacked.getFullName());
			packedToUpdate.setMobileNumber(updatedPacked.getMobileNumber());
			packedToUpdate.setAddress(updatedPacked.getAddress());
			packedToUpdate.setFirmName(updatedPacked.getFirmName());
			return this.packedRepository.save(packedToUpdate);
		}
		throw new IllegalArgumentException("Officer not found");
	}

	public Packed deletePackedDetails(Long id) {
		Optional<Packed> existingPacked = this.packedRepository.findById(id);
		if (existingPacked.isPresent()) {
			Packed packedToUpdate = existingPacked.get();
			packedToUpdate.setAadharNumber(null);
			packedToUpdate.setFullName(null);
			packedToUpdate.setMobileNumber(null);
			packedToUpdate.setAddress(null);
			packedToUpdate.setFirmName(null);
			return (Packed) this.packedRepository.save(packedToUpdate);
		}
		throw new IllegalArgumentException("Packed not found");
	}

	public String processAndSaveDetails(Long packedId) {
		if (this.packedRepository == null || this.inscanService == null)
			throw new IllegalStateException("Packed repository or Inscan service not initialized");
		Optional<Packed> optionalPacked = this.packedRepository.findById(packedId);
		if (optionalPacked.isPresent()) {
			Packed packed = optionalPacked.get();
			StringBuilder detailsBuilder = (new StringBuilder("PT/HPNSK/")).append(packed.getId());
			String department = "Driver";
			String sub_department = "PT";
			String details = "PT/HPNSK/";
			Long ofcid = packed.getId();
			String name = packed.getFullName();
			String aadharNumber = packed.getAadharNumber();
			String mobile = packed.getMobileNumber();
			String address = packed.getAddress();
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
		throw new IllegalArgumentException("Packed not found");
	}

	public Packed getDetailsByAadharNumber(String aadharNumber) {
		return this.packedRepository.findByAadharNumber(aadharNumber);
	}

	public String getFullName(Long entityId) {
		Packed packed = this.packedRepository.findById(entityId).orElse(null);
		if (packed != null) {
			String fullName = packed.getFullName();
			return fullName;
		}
		return "Unknown Packed";
	}

	public String processAndSaveLicenseGateDetails(Long packedId) {
		if (this.packedRepository == null || this.licenseGateService == null)
			throw new IllegalStateException("Packed repository or LicenseGateService service not initialized");
		Optional<Packed> optionalPacked = this.packedRepository.findById(packedId);
		if (optionalPacked.isPresent()) {
			Packed packed = optionalPacked.get();
			StringBuilder detailsBuilder = (new StringBuilder("PT/HPNSK/")).append(packed.getId());
			String department = "Operation";
			String sub_department = "PT";
			String details = detailsBuilder.toString();
			Long ofcId = packed.getId();
			String name = packed.getFullName();
			String aadharNumber = packed.getAadharNumber();
			String mobile = packed.getMobileNumber();
			String address = packed.getAddress();
			String contractor = null;
			String firmName = null;

			Licensegate licenseGate = this.licenseGateService.findByAadhar(aadharNumber);
			if (licenseGate == null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, packedId,
						department, sub_department, contractor, firmName);
				return "In";
			}
			if (licenseGate.getExitDateTime() != null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, packedId,
						department, sub_department, contractor, firmName);
				return "Scan In";
			}
			this.licenseGateService.updateDetailsToLicenseGate(licenseGate);
			return "Scan Out";
		}
		throw new IllegalArgumentException("Packed not found");
	}
}
