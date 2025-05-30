package com.in_out.repo;


import com.in_out.model.Sec;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecRepository extends JpaRepository<Sec, Long> {
  Sec findByAadharNumber(String paramString);
}
