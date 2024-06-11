package com.andygomez.register_login.flow.application;

import com.andygomez.register_login.flow.model.MailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailUseCase {

    @Autowired
    private JavaMailSender mailSender;

    @Value("$(spring.email.username)")
    private String fromMail;

    public void sendMail(String email, MailModel mailStructure){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(fromMail);
        simpleMailMessage.setSubject(mailStructure.getSubject());
        simpleMailMessage.setText(mailStructure.getMessage());
        simpleMailMessage.setTo(email);

        mailSender.send(simpleMailMessage);
    }
}
