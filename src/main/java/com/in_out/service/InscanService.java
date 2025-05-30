package com.in_out.service;

import com.in_out.model.Inscan;
import com.in_out.repo.InscanRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscanService {
	@Autowired
	private InscanRepository inscanRepository;

	public void saveDetailsToInscan(String details, String name, String aadharNumber, String mobile, String address,
			Long ofcid, String department, String sub_department, String mainGateStatus, String contractor,
			String firmName) {
		Inscan inscan = new Inscan();
		inscan.setDetails(details);
		inscan.setOfcid(ofcid);
		inscan.setName(name);
		inscan.setAadharNumber(aadharNumber);
		inscan.setMobile(mobile);
		inscan.setAddress(address);
		inscan.setDepartment(department);
		inscan.setSub_department(sub_department);
		inscan.setMainGateSatus(mainGateStatus);
		inscan.setContractor(contractor);
		inscan.setFirmName(firmName);

		this.inscanRepository.save(inscan);
	}

	public List<Inscan> getAllInscanDetails() {
		return this.inscanRepository.findAll();
	}

	public long countInScansByAadharAndDate(String aadharNumber, LocalDateTime start, LocalDateTime end) {
		return inscanRepository.countByAadharNumberAndEntryDateTimeBetweenAndExitDateTimeIsNull(aadharNumber, start,
				end);
	}

	public List<Inscan> getAllInscanDetailsForCurrentDay() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.inscanRepository.findByEntryDateTimeBetweenOrderByDetails(startDate, endDate);
	}

	public Long getCountOfInscanDetailsForCurrentDay() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.inscanRepository.countByEntryDateTimeBetween(startDate, endDate);
	}

	public List<Inscan> findByEntryDateTimeBetweenOrderByDetailsForOperation() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.inscanRepository.findByEntryDateTimeBetweenOrderByDetailsForOperation(startDate, endDate);
	}

	public Long countByEntryDateTimeBetweenForOperation() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date startDate = Date
				.from(currentDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(
				currentDateTime.plusDays(1L).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		return this.inscanRepository.countByEntryDateTimeBetweenForOperation(startDate, endDate);
	}

	public List<Inscan> findByEntryDateTimeBetweenOrderByDetailsForProject() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.inscanRepository.findByEntryDateTimeBetweenOrderByDetailsForProject(startDate, endDate);
	}

	public Long countByEntryDateTimeBetweenForProject() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date startDate = Date
				.from(currentDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(
				currentDateTime.plusDays(1L).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		return this.inscanRepository.countByEntryDateTimeBetweenForProject(startDate, endDate);
	}

	public List<Inscan> findByEntryDateTimeBetweenOrderByDetailsForVisitor() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime startOfDay = currentDateTime.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1L);
		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
		return this.inscanRepository.findByEntryDateTimeBetweenOrderByDetailsForVisitor(startDate, endDate);
	}

	public Long countByEntryDateTimeBetweenForVisitor() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Date startDate = Date
				.from(currentDateTime.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(
				currentDateTime.plusDays(1L).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
		return this.inscanRepository.countByEntryDateTimeBetweenForVisitor(startDate, endDate);
	}

	public Inscan findByAadhar(String aadharNumber) {
		return this.inscanRepository.findByAadharNumber(aadharNumber);
	}

	public void updateDetailsToInscan(Inscan inscan) {
		inscan.setExitDateTime(new Date());
		inscan.setMainGateSatus("Y");
		this.inscanRepository.save(inscan);
	}

	public Inscan findByAadharExitDateTime(String aadharNumber, Date exitDateTime) {
		return this.inscanRepository.findByAadharNumber(aadharNumber);
	}

	public List<Inscan> generateReportDataMainGate(String fromDate, String toDate, String department,
			String subDepartment, String name) {
		LocalDateTime startDateTime = LocalDateTime.parse(fromDate + "T00:00");
		LocalDateTime endDateTime = LocalDateTime.parse(toDate + "T23:59");
		Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
		if ("All".equalsIgnoreCase(department) && "All".equalsIgnoreCase(subDepartment)
				&& (name == null || "All".equalsIgnoreCase(name))) {
			return this.inscanRepository.findByEntryDateTimeBetweenOrderByDetails(startDate, endDate);
		}
		if ("All".equalsIgnoreCase(subDepartment)) {
			if (name != null && !"All".equalsIgnoreCase(name)) {
				return this.inscanRepository
						.findByEntryDateTimeBetweenAndDepartmentAndNameContainingIgnoreCaseOrderByDetails(startDate,
								endDate, department, name);
			}
			return this.inscanRepository.findByEntryDateTimeBetweenAndDepartmentOrderByDetails(startDate, endDate,
					department);
		}
		if (name != null && !"All".equalsIgnoreCase(name)) {
			return this.inscanRepository
					.findByEntryDateTimeBetweenAndDepartmentAndSubDepartmentAndNameContainingIgnoreCaseOrderByDetails(
							startDate, endDate, department, subDepartment, name);
		}
		return this.inscanRepository.findByEntryDateTimeBetweenAndDepartmentAndSubDepartmentOrderByDetails(startDate,
				endDate, department, subDepartment);
	}

	public String getMainGateSatus(Long entityId, String entityType) {
		return this.inscanRepository.findBySubDepartmentAndExitDateTimeAndMainGateStatus(entityId, entityType);
	}

	public void updateNullExitDateTime() {
		List<Inscan> nullExitDateTimeList = this.inscanRepository.findByExitDateTimeIsNull();
		for (Inscan inscan : nullExitDateTimeList)
			inscan.setExitDateTime(new Date());
		this.inscanRepository.saveAll(nullExitDateTimeList);
	}

	public List<Map<String, Object>> generateNightReportData(String date, String fromTime, String toTime)
			throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		// Parse date and time
		Date startDateTime = formatter.parse(date + " " + fromTime);
		Date endDateTime = formatter.parse(date + " " + toTime);

		System.out.println("Start Time: " + startDateTime + " | End Time: " + endDateTime); // Debugging

		// Fetch data from the repository
		List<Object[]> results = inscanRepository.findCombinedGateData(date, fromTime, toTime);
		List<Map<String, Object>> reportData = new ArrayList<>();

		// Map the results to a List of Maps
		for (Object[] row : results) {
			Map<String, Object> rowData = new HashMap<>();
			rowData.put("id", row[0]);
			rowData.put("aadharNumber", row[1]);
			rowData.put("address", row[2]);
			rowData.put("department", row[3]);
			rowData.put("details", row[4]);
			rowData.put("entryDateTime", row[5]);
			rowData.put("exitDateTime", row[6]);
			rowData.put("mobile", row[7]);
			rowData.put("name", row[8]);
			rowData.put("ofcId", row[9]);
			rowData.put("subDepartment", row[10]);
			rowData.put("source", row[11]); // Add the source column

			reportData.add(rowData);
		}

		return reportData;
	}
}
