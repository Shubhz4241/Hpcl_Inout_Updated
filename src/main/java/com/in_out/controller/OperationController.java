package com.in_out.controller;


import com.in_out.model.Mathadi;
import com.in_out.service.ContractorService;
import com.in_out.service.ContractorworkmanService;
import com.in_out.service.EmployeeService;
import com.in_out.service.FegService;
import com.in_out.service.GatService;
import com.in_out.service.MathadiService;
import com.in_out.service.OfficerService;
import com.in_out.service.SecService;
import com.in_out.service.TatService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OperationController {
  @Autowired
  private OfficerService officerService;
  
  @Autowired
  private EmployeeService employeeService;
  
  @Autowired
  private ContractorService contractorService;
  
  @Autowired
  private ContractorworkmanService contractorworkmanService;
  
  @Autowired
  private MathadiService mathadiService;
  
  @Autowired
  private GatService gatService;
  
  @Autowired
  private TatService tatService;
  
  @Autowired
  private FegService fegService;
  
  @Autowired
  private SecService secService;
  
  @PostMapping({"/submitOperators"})
  public String submitOperators(@RequestBody List<Integer> operatorData) {
    if (operatorData != null && !operatorData.isEmpty()) {
      this.officerService.addOfficer(operatorData);
      return "200";
    } 
    return "ERROR";
  }
  
  @PostMapping({"/submitEmployee"})
  public String submitEmployee(@RequestBody List<Integer> employeeData) {
    if (employeeData != null && !employeeData.isEmpty()) {
      this.employeeService.addEmployee(employeeData);
      return "200";
    } 
    return "ERROR";
  }
  
  @PostMapping({"/submitContractor"})
  public String submitContractor(@RequestBody List<Integer> contractorData) {
    if (contractorData != null && !contractorData.isEmpty()) {
      this.contractorService.addContractor(contractorData);
      return "200";
    } 
    return "ERROR";
  }
  
  @PostMapping({"/submitContractorworkman"})
  public String submitContractorworkman(@RequestBody List<Integer> contractorworkmanData) {
    if (contractorworkmanData != null && !contractorworkmanData.isEmpty()) {
      this.contractorworkmanService.addContractorworkman(contractorworkmanData);
      return "200";
    } 
    return "ERROR";
  }
  @PostMapping("/submitMathadi")
  public ResponseEntity<String> submitMathadi(@RequestBody List<Integer> mathadiData) {
      if (mathadiData == null || mathadiData.isEmpty()) {
          return ResponseEntity.badRequest().body("ERROR: No data provided");
      }
      
      List<Mathadi> savedMathadi = mathadiService.addMathadi(mathadiData);
      return ResponseEntity.ok("200");
  }
  
  
  @PostMapping({"/submitGats"})
  public String submitGats(@RequestBody List<Integer> gatData) {
    if (gatData != null && !gatData.isEmpty()) {
      this.gatService.addGat(gatData);
      return "200";
    } 
    return "ERROR";
  }
  
  @PostMapping({"/submitTats"})
  public String submitTats(@RequestBody List<Integer> tatData) {
    if (tatData != null && !tatData.isEmpty()) {
      this.tatService.addTat(tatData);
      return "200";
    } 
    return "ERROR";
  }
  
  @PostMapping({"/submitFegs"})
  public String submitFegs(@RequestBody List<Integer> fegData) {
    if (fegData != null && !fegData.isEmpty()) {
      this.fegService.addFeg(fegData);
      return "200";
    } 
    return "ERROR";
  }
  
  @PostMapping({"/submitSec"})
  public String submitSecs(@RequestBody List<Integer> secData) {
    if (secData != null && !secData.isEmpty()) {
      this.secService.addSec(secData);
      return "200";
    } 
    return "ERROR";
  }
}