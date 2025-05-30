package com.in_out.repo;


import com.in_out.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
  @Query("Select u from User u where u.userName=:userName")
  User findbyuserName(@Param("userName") String paramString);
  
  @Query("Select u from User u where u.Role=:Role")
  List<User> finfbyRole(@Param("Role") String paramString);
  
  User findByUserName(String paramString);
}
