package com.bankapp.service;

import com.bankapp.dto.TransactionDto;
import com.bankapp.entity.Transaction;

public interface TransactionService {

    void saveTransaction(TransactionDto transactionDto);
}
