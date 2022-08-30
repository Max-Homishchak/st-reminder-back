package com.homishchak.streminder.service;

import com.homishchak.streminder.model.Task;
import com.homishchak.streminder.model.Visitor;
import com.homishchak.streminder.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class TaskServiceImpl implements TaskService{

    private TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void saveAllTasks(Visitor visitor) {

        for(Task t: visitor.getTasks()) {
            t.setVisitor(visitor);
            taskRepository.save(t);
        }
    }
}
