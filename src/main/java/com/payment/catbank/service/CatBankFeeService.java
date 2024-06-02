package com.payment.catbank.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CatBankFeeService {

    public void transferFee(BigDecimal amount) {
        System.out.println("Bank fee");
    }
}
