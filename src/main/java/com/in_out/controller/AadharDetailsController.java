package com.in_out.controller;
import com.in_out.model.AadharNumberDetails;
import com.in_out.service.AadharNumberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/aadhar-details"})
public class AadharDetailsController {
  @Autowired
  private AadharNumberDetailsService aadharNumberDetailsService;
  
  @GetMapping({"/{aadharNumber}"})
  public ResponseEntity<AadharNumberDetails> getAadharDetailsByAadharNumber(@PathVariable String aadharNumber) {
    try {
      AadharNumberDetails aadharDetails = this.aadharNumberDetailsService.getAadharNumberDetailsByAadharNumber(aadharNumber);
      if (aadharDetails != null)
        return ResponseEntity.ok(aadharDetails); 
      return ResponseEntity.notFound().build();
    } catch (Exception e) {
      return ResponseEntity.status((HttpStatusCode)HttpStatus.INTERNAL_SERVER_ERROR).build();
    } 
  }
}
