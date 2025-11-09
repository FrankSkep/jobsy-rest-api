package com.fran.jobsy.app.service.mail;

public interface MailService {
    void sendEmail(String to, String subject, String body);
}
