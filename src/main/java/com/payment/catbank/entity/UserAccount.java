package com.payment.catbank.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    private String id;
    private BigDecimal balance;
    private String iban;
    private int transferCount;
}
