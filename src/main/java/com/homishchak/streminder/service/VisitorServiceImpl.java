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
import java.util.Optional;

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

        helper.setFrom("st.reminder.bot@gmail.com");
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

        Visitor user = visitorRepository.findVisitorForCodeCheck(email);

        visitorRepository.deleteByEmailAndVerificationCode(email, code);

        if(code == user.getVerificationCode()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Visitor save(Visitor visitor) throws MessagingException {

        sendFirstMessage(visitor);

        visitorRepository.save(visitor);

        saveVisitorTasks(visitor);

        return visitor;
    }


    @Override
    @Transactional
    public Visitor updateVisitor(Visitor visitor) throws MessagingException {

        visitorRepository.deleteByEmail(visitor.getEmail());

        return save(visitor);
    }

    @Override
    public Visitor findForChange(String email) {

        return visitorRepository.findVisitorForChange(email);
    }

    private void saveVisitorTasks(Visitor visitor) throws MessagingException {

        taskService.saveAllTasks(visitor);
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

        helper.setFrom("st.reminder.bot@gmail.com");
        helper.setTo(visitor.getEmail());
        helper.setSubject("Greeting");
        helper.setText(mailContent, true);

        javaMailSender.send(message);
    }
}
