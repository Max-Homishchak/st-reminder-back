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

            return new ResponseEntity<>(HttpStatus.OK);
        }else{

            visitorService.sendVerificationCode(email);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addVisitor(@RequestBody Visitor visitor) throws MessagingException {

        visitorService.save(visitor);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<Visitor> updateVisitor(@RequestBody Visitor visitor) throws MessagingException{

        visitorService.updateVisitor(visitor);

        return new ResponseEntity<Visitor>(HttpStatus.OK);
    }

    @GetMapping("/checkEmail/{email}")
    public ResponseEntity<Visitor> checkExistence(@PathVariable("email") String email) {

        Visitor visitor = visitorService.findForChange(email);

        if(visitor == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }else{
            return new ResponseEntity<>(visitor, HttpStatus.OK);
        }

    }
}
