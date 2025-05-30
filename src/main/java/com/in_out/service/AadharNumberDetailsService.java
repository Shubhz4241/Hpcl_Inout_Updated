package com.in_out.service;


import com.in_out.model.AadharNumberDetails;
import com.in_out.repo.AadharNumberDetailsRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AadharNumberDetailsService {
  @Autowired
  private AadharNumberDetailsRepository aadharNumberDetailsRepository;
  
  public AadharNumberDetails saveAadharNumberDetails(AadharNumberDetails aadharNumberDetails) {
    return (AadharNumberDetails)this.aadharNumberDetailsRepository.save(aadharNumberDetails);
  }
  
  public Optional<AadharNumberDetails> getAadharNumberDetailsById(Long id) {
    return this.aadharNumberDetailsRepository.findById(id);
  }
  
  public List<AadharNumberDetails> getAllAadharNumberDetails() {
    return this.aadharNumberDetailsRepository.findAll();
  }
  
  public void deleteAadharNumberDetails(Long id) {
    this.aadharNumberDetailsRepository.deleteById(id);
  }
  
  public AadharNumberDetails getAadharNumberDetailsByAadharNumber(String aadharNumber) {
    return this.aadharNumberDetailsRepository.findByAadharNumber(aadharNumber);
  }
}
