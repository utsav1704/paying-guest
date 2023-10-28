package com.pg.email.service;

import com.pg.owner.entity.Owner;
import com.pg.owner.entity.Property;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;

    @KafkaListener(topics = "pg_owner", groupId = "pg")
    public void consume1(Owner owner) {
        StringBuilder message = new StringBuilder();
        StringBuilder subject = new StringBuilder();
        if (owner.getIsVerified()) {
            log.info("Sending owner verified message.");
            subject.append("Owner Verified!!");
            message.append("Hello ");
            message.append(owner.getFirstName());
            message.append("\n\n");
            message.append("You are verified. Now you can add your properties");
        } else {
            log.info("Sending owner added message.");
            subject.append("Owner added!!");
            message.append("Hello ");
            message.append(owner.getFirstName());
            message.append("\n\n");
            message.append("Thank you for choosing us. You can add/manage property once you are verified.");
        }

        sendMail(owner.getEmail(), subject.toString(), message.toString());
    }

    @KafkaListener(topics = "pg_property", groupId = "pg")
    public void consume2(Property property) {
        StringBuilder message = new StringBuilder();
        StringBuilder subject = new StringBuilder();
        if (property.getIsVerified()) {
            log.info("Sending property verified message.");
            subject.append("Property Verified!!");
            message.append("Hello ");
            message.append(property.getOwner().getFirstName());
            message.append("\n\n");
            message.append("Your ");
            message.append(property.getPropertyName());
            message.append(" is verified. Now Client can see your properties for rent");
        } else {
            log.info("Sending property added message.");
            subject.append("Property added!!");
            message.append("Hello ");
            message.append(property.getOwner().getFirstName());
            message.append("\n\n");
            message.append("Your ");
            message.append(property.getPropertyName());
            message.append(" is added. We sent you mail once It is verified.");
        }
        sendMail(property.getOwner().getEmail(), subject.toString(), message.toString());
    }

    private void sendMail(String email, String subject, String msg){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(msg);
        emailSender.send(simpleMailMessage);
    }
}
