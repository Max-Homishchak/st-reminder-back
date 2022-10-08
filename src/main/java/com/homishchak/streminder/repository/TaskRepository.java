package com.homishchak.streminder.repository;

import com.homishchak.streminder.model.Task;
import com.homishchak.streminder.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    public List<Task> findAllByVisitorId(Long id);

    public List<Task> findByTimeLessThanEqual(String time);

    public List<Task> findByTimeLessThanEqualAndAlreadyNotifiedFalse(String time);
}
