package com.homishchak.streminder.service;

import com.homishchak.streminder.model.Task;
import com.homishchak.streminder.model.Visitor;
import com.homishchak.streminder.repository.TaskRepository;
import com.homishchak.streminder.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class TaskServiceImpl implements TaskService{

    private TaskRepository taskRepository;
    private VisitorRepository visitorRepository;
    private JavaMailSender javaMailSender;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, VisitorRepository visitorRepository, JavaMailSender javaMailSender) {
        this.taskRepository = taskRepository;
        this.visitorRepository = visitorRepository;
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void saveAllTasks(Visitor visitor) {

        for(Task t: visitor.getTasks()) {
            t.setVisitor(visitor);
            taskRepository.save(t);
        }
    }

    @Scheduled(fixedRate = 30000)
    @Override
    public void notifyAllUpcomingTasks() throws MessagingException {

        List<Task> tasksToBeNotified = Stream.of(findUpcomingTasks(), findTasksBeforeReminder())
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());

        for(Task task: tasksToBeNotified) {
            sendRemindingMail(task);
        }

    }

    private void sendRemindingMail(Task task) throws MessagingException {

        Visitor visitor = task.getVisitor();

        String mailContent = "<p><b>StReminder reminds you</b></p>\n";

        if(task.isAlreadyNotified()) {
            mailContent += "task: " + task.getTask() + "is planned on NOW";
            taskRepository.delete(task);

        }else if(!task.isAlreadyNotified()){
            mailContent += visitor.getBeforeReminderTime() + " minutes is left before task: " + task.getTask();
            task.setAlreadyNotified(true);
            taskRepository.save(task);
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("st.reminder.bot@gmail.com");
        helper.setTo(visitor.getEmail());
        helper.setSubject("Reminding");
        helper.setText(mailContent, true);

        javaMailSender.send(message);
    }

    private List<Task> findUpcomingTasks() {

        String currentTime = new String(String.valueOf(LocalDateTime.now()));

        return taskRepository.findByTimeLessThanEqual(currentTime);
    }

    private List<Task> findTasksBeforeReminder() {

        List<Task> resultTasks = new ArrayList<>();

        String currentTime;

        for(Visitor visitor: visitorRepository.findAll()) {

            currentTime = new String(String.valueOf(LocalDateTime.now().plusSeconds(visitor.getBeforeReminderTime() * 60)));

            resultTasks.addAll(taskRepository.findByTimeLessThanEqualAndAlreadyNotifiedFalse(currentTime));
        }

        return resultTasks;
    }
}
