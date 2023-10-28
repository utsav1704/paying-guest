package com.pg.apigateway.domain;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionResponseModel {
    private String errorCode;
    private String err;
    private String errorDetails;
    private Date date;
}
