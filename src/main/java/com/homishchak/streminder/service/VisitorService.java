package com.homishchak.streminder.service;

import com.homishchak.streminder.model.Task;
import com.homishchak.streminder.model.Visitor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.List;

@Service
public interface VisitorService {

    public void saveVisitorTasks(Visitor visitor) throws MessagingException;

    public int sendVerificationCode(String to) throws MessagingException;

    public boolean validateCode(int code, String email);
}
