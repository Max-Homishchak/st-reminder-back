package com.homishchak.streminder.repository;

import com.homishchak.streminder.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    void deleteByEmail(String email);
    Visitor findByEmail(String email);
}
