package com.bankapp.service;

import com.bankapp.dto.*;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount (CreditDebitRequest request);

    BankResponse debitAccount (CreditDebitRequest request);

    BankResponse transferRequest(TransferRequest request);


}
