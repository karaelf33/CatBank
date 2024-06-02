package com.payment.catbank.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BlikTaxService {

    public void applyTax(BigDecimal taxAmount) {
        System.out.println("apply tax");
    }
}
