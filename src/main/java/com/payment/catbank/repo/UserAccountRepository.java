package com.payment.catbank.repo;

import com.payment.catbank.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

    @Query("SELECT u.balance FROM UserAccount u WHERE u.iban = :iban")
    BigDecimal findBalanceByIban(String iban);

    @Query("SELECT u.transferCount FROM UserAccount u WHERE u.iban = :iban")
    int findTransferCountByIban(@Param("iban") String iban);
}
