package com.bankapp.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {

    private String recipient;
    private String messageBody;
    private String subject;
    private String attachment;

}
