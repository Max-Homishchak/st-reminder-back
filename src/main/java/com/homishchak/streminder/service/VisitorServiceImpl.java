package com.homishchak.streminder.service;

import com.homishchak.streminder.model.Task;
import com.homishchak.streminder.model.Visitor;
import com.homishchak.streminder.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class VisitorServiceImpl implements VisitorService {

    private JavaMailSender javaMailSender;
    private VisitorRepository visitorRepository;
    private TaskService taskService;

    @Autowired
    public VisitorServiceImpl(JavaMailSender javaMailSender, VisitorRepository visitorRepository, TaskService taskService) {
        this.javaMailSender = javaMailSender;
        this.visitorRepository = visitorRepository;
        this.taskService = taskService;
    }

    public int sendVerificationCode(String to) throws MessagingException {

        int code = (int)(Math.random() * Math.pow(10, 6));

        String mailContent = "<h2>Welcome to StReminder</h2>" +
                "<p><b>CODE: </b>" + code + "</p>";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("maksimhomisak813@gmail.com");
        helper.setTo(to);
        helper.setSubject("Verification code");
        helper.setText(mailContent, true);

        javaMailSender.send(message);

        visitorRepository.save(Visitor.builder()
                        .email(to)
                        .verificationCode(code)
                        .build());

        return code;
    }

    @Transactional
    public boolean validateCode(int code, String email) {

        Visitor user = visitorRepository.findByEmail(email);

        visitorRepository.deleteByEmail(email);

        if(code == user.getVerificationCode()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void saveVisitorTasks(Visitor visitor) throws MessagingException {

        save(visitor);

        taskService.saveAllTasks(visitor);
    }

    private Visitor save(Visitor visitor) throws MessagingException {

        sendFirstMessage(visitor);

        return visitorRepository.save(visitor);
    }

    private void sendFirstMessage(Visitor visitor) throws MessagingException {

        String mailContent = "<p><b>You have signed for StReminder mailing list</b></p>\n";

        List<Task> taskList = visitor.getTasks();

        if(taskList == null) {
            mailContent += "You have no tasks";
        }else{
            for(Task t: taskList){
                mailContent += "task: " + t.getTask() + "; time: " + t.getTime() + ";\n";
            }
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("maksimhomisak813@gmail.com");
        helper.setTo(visitor.getEmail());
        helper.setSubject("Greeting");
        helper.setText(mailContent, true);

        javaMailSender.send(message);
    }
}
