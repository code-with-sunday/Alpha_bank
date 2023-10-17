package com.bankapp.service;

import com.bankapp.dto.request.CreditDebitRequest;
import com.bankapp.dto.request.EnquiryRequest;
import com.bankapp.dto.request.TransferRequest;
import com.bankapp.dto.request.UserRequest;
import com.bankapp.dto.response.BankResponse;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount (CreditDebitRequest request);

    BankResponse debitAccount (CreditDebitRequest request);

    BankResponse transferRequest(TransferRequest request);


}
