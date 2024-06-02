package com.payment.catbank.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PaymentRequest {

    private String paymentType;
    private String senderIban;
    private String receiverIban;
    private BigDecimal amount;

}
