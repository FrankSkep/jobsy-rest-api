package com.fran.jobsy.app.service;

public interface MailService {
    void sendEmail(String to, String subject, String body);

    void sendEmailWithAttachment(String to, String subject, String text, byte[] attachment, String filename);
}
