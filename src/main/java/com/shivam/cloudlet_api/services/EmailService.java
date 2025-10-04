package com.shivam.cloudlet_api.services;

import com.shivam.cloudlet_api.dto.EmailDetails;

import jakarta.mail.MessagingException;

public interface EmailService {
    Boolean sendSimpleMail(EmailDetails details);

    Boolean sendMailWithAttachment(EmailDetails details) throws MessagingException;

}
