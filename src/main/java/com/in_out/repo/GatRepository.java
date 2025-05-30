package com.in_out.repo;


import com.in_out.model.Gat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GatRepository extends JpaRepository<Gat, Long> {
  Gat findByAadharNumber(String paramString);
}
