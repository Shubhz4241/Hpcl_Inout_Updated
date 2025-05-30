package com.in_out.repo;


import com.in_out.model.transporter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransporterRepository extends JpaRepository<transporter, Long> {
  transporter findByAadharNumber(String paramString);
  transporter findByTruckNumber(String truckNumber);
}
