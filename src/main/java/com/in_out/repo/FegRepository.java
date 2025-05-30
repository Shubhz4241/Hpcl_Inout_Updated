package com.in_out.repo;


import com.in_out.model.Feg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FegRepository extends JpaRepository<Feg, Long> {
  Feg findByAadharNumber(String paramString);
}
