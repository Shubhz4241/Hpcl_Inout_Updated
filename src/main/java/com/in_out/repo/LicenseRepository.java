package com.in_out.repo;


import com.in_out.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

public interface LicenseRepository extends JpaRepository<License, Integer> {
  License findBylicensekey(@RequestParam("licensekey") String paramString);
  
  boolean existsByLicensekey(String paramString);
}
