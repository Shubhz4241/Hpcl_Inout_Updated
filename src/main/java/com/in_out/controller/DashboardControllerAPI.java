package com.in_out.controller;


import com.in_out.service.DriverService;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import com.in_out.service.OperationService;
import com.in_out.service.ProjectService;
import com.in_out.service.VisitorService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/dashboard-details"})
public class DashboardControllerAPI {
  @Autowired
  private OperationService operationService;
  
  @Autowired
  private DriverService driverService;
  
  @Autowired
  private ProjectService projectService;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService LicensegateService;
  
  @Autowired
  private VisitorService visitorService;
  
  @GetMapping({"/dashboard-count"})
  public ResponseEntity<Object> dashboard() {
    Map<String, Long> counts = new HashMap<>();
    Long totalInscanRecordCount = this.inscanService.getCountOfInscanDetailsForCurrentDay();
    counts.put("totalInscanRecordCount", totalInscanRecordCount);
    Long totalInscanRecordCountForOperation = this.inscanService.countByEntryDateTimeBetweenForOperation();
    counts.put("totalInscanRecordCountForOperation", totalInscanRecordCountForOperation);
    Long totalInscanRecordCountForProject = this.inscanService.countByEntryDateTimeBetweenForProject();
    counts.put("totalInscanRecordCountForProject", totalInscanRecordCountForProject);
    Long totalInscanRecordCountForVisitor = this.inscanService.countByEntryDateTimeBetweenForVisitor();
    counts.put("totalInscanRecordCountForVisitor", totalInscanRecordCountForVisitor);
    Long totalLicenseGateRecordCountForOperation = this.LicensegateService.countByEntryDateTimeBetweenForOperationLicenseGate();
    counts.put("totalLicenseGateRecordCountForOperation", totalLicenseGateRecordCountForOperation);
    Long totalLicenseGateRecordCount = this.LicensegateService.getCountOfLicensegateDetailsForCurrentDay();
    counts.put("totalLicenseGateRecordCount", totalLicenseGateRecordCount);
    Long totalLicenseGateRecordCountForProject = this.LicensegateService.countByEntryDateTimeBetweenForProjectLicensegate();
    counts.put("totalLicenseGateRecordCountForProject", totalLicenseGateRecordCountForProject);
    Long totalLicenseGateRecordCountForVisitor = this.LicensegateService.countByEntryDateTimeBetweenForVisitorLicensegate();
    counts.put("totalLicenseGateRecordCountForVisitor", totalLicenseGateRecordCountForVisitor);
    Long totalOperationRecordCount = this.operationService.getOperationTotalRecordCount();
    counts.put("totalOperationRecordCount", totalOperationRecordCount);
    Long totalDriverRecordCount = this.driverService.getDriverTotalRecordCount();
    counts.put("totalDriverRecordCount", totalDriverRecordCount);
    Long totalProjectRecordCount = this.projectService.getProjectTotalRecordCount();
    counts.put("totalProjectRecordCount", totalProjectRecordCount);
    Long totalVisitorRecordCount = Long.valueOf(this.visitorService.countVisitorsWithFullNameNotNull());
    counts.put("totalVisitorRecordCount", totalVisitorRecordCount);
    return new ResponseEntity(counts, (HttpStatusCode)HttpStatus.OK);
  }
}
