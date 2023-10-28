package com.bankapp.service;

import com.bankapp.dto.request.*;
import com.bankapp.dto.response.BankResponse;
import com.bankapp.entity.User;

import java.util.List;

public interface UserService {

    BankResponse createAccount(UserRequest userRequest);

    BankResponse balanceEnquiry(EnquiryRequest request);

    String nameEnquiry(EnquiryRequest request);

    BankResponse creditAccount (CreditDebitRequest request);

    BankResponse debitAccount (CreditDebitRequest request);

    BankResponse transferRequest(TransferRequest request);

    BankResponse updateUserDetails(UserRequest userRequest, Long id);

    List<UserRequest> getAllUser();

    BankResponse deleteUserInfo(UserRequest userRequest,String email);

    BankResponse login(LoginDto loginDto);


}
