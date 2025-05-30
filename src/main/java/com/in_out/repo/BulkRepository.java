package com.in_out.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.in_out.model.Bulk;

public interface BulkRepository extends JpaRepository<Bulk, Long> {
  Bulk findByAadharNumber(String paramString);
  Bulk findByTruckNumber(String truckNumber); // Add this method
}  