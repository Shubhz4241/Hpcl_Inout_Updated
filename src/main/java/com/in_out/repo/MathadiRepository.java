package com.in_out.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.in_out.model.Mathadi;

@Repository
public interface MathadiRepository extends JpaRepository<Mathadi, Long> {
	Mathadi findByAadharNumber(String paramString);
	  
	  @Query("SELECT m.fullName FROM Mathadi m WHERE m.fullName IS NOT NULL")
	  List<String> findFullNames();
}

