package com.shivam.store_api.services;

import com.shivam.store_api.models.EmailDetails;

import jakarta.mail.MessagingException;

public interface EmailService {
    Boolean sendSimpleMail(EmailDetails details);

    Boolean sendMailWithAttachment(EmailDetails details) throws MessagingException;

}
