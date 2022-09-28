package com.homishchak.streminder.service;

import com.homishchak.streminder.model.Task;
import com.homishchak.streminder.model.Visitor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
public interface TaskService {

    public void saveAllTasks(Visitor visitor);

    public void notifyAllUpcomingTasks() throws MessagingException;
}
