package com.bankapp.service;

import com.bankapp.dto.request.TransactionDto;

public interface TransactionService {

    void saveTransaction(TransactionDto transactionDto);
}
