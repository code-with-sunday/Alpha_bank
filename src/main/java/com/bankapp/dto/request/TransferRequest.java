package com.bankapp.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {

    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
}
