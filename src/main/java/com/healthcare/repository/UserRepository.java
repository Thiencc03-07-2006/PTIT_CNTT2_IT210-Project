package com.healthcare.repository;

import com.healthcare.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    // Tìm các User có Role là PATIENT và khớp tên hoặc số điện thoại trong Profile
    @Query("SELECT u FROM User u JOIN u.profile p " +
            "WHERE u.role = 'PATIENT' " +
            "AND (p.fullName LIKE %:query% OR p.phone LIKE %:query%)")
    List<User> searchPatients(@Param("query") String query);

    @Query("SELECT u FROM User u WHERE u.role = 'PATIENT'")
    List<User> findAllPatients();
}
