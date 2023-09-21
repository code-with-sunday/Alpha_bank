package com.bankapp.service;

import com.bankapp.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlert(EmailDetails emailDetails);
    void debitEmailAlert(EmailDetails emailDetails);

    void creditEmailAlert(EmailDetails emailDetails);

}
