package com.bankapp.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;
    private String status;
}
