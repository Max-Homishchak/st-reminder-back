package com.homishchak.streminder.repository;

import com.homishchak.streminder.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    void deleteByEmailAndVerificationCode(String email, int code);

    void deleteByEmail(String email);

    @Query(value = "SELECT * FROM visitor v WHERE v.email = :email AND v.verification_code = 0", nativeQuery = true)
    Visitor findVisitorForChange(@Param("email") String email);

    @Query(value = "SELECT * FROM visitor v WHERE v.email = :email AND v.verification_code <> 0", nativeQuery = true)
    Visitor findVisitorForCodeCheck(@Param("email") String email);

}
