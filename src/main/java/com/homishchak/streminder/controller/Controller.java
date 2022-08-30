package com.homishchak.streminder.controller;

import com.homishchak.streminder.model.Task;
import com.homishchak.streminder.model.Visitor;
import com.homishchak.streminder.service.TaskService;
import com.homishchak.streminder.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping("/")
public class Controller {

    private final VisitorService visitorService;
    private final TaskService taskService;

    @Autowired
    public Controller(VisitorService visitorService, TaskService taskService) {
        this.visitorService = visitorService;
        this.taskService = taskService;
    }

    @PostMapping("/send/{email}")
    public ResponseEntity<?> sendMessage(@PathVariable("email") String email) throws MessagingException {

        visitorService.sendVerificationCode(email);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/checkCode/{email}/{code}")
    public ResponseEntity<?> checkCode(@PathVariable("email") String email, @PathVariable("code") int code) throws MessagingException {

        if(visitorService.validateCode(code, email)){
        }else{

            System.out.println("Invalid Code");
            visitorService.sendVerificationCode(email);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addVisitor(@RequestBody Visitor visitor) throws MessagingException {

        visitorService.saveVisitorTasks(visitor);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
