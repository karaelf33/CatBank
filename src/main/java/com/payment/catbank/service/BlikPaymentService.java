package com.payment.catbank.service;

import com.payment.catbank.exception.UnsupportedPaymentTypeException;
import com.payment.catbank.model.PaymentRequest;
import com.payment.catbank.repo.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BlikPaymentService {

    private final BlikTaxService blikTaxService;
    private final CatBankFeeService catBankFeeService;
    private final UserAccountRepository userAccountRepository;

    public BigDecimal paymentWithBlik(PaymentRequest paymentRequest) {
        if (!paymentRequest.getPaymentType().equals("BLIK")) {
            throw new UnsupportedPaymentTypeException("Please choose Blik payment type");
        }

        BigDecimal transferAmount = paymentRequest.getAmount();
        blinkTaxDeduction(transferAmount);
        bankFee(transferAmount);

        String senderIban = paymentRequest.getSenderIban();
        BigDecimal balanceByIban = userAccountRepository.findBalanceByIban(senderIban);
        int transferCountByIban = userAccountRepository.findTransferCountByIban(senderIban);
        if (transferCountByIban > 99) {
            BigDecimal award = new BigDecimal("100.00");
            return balanceByIban.subtract(transferAmount).add(award);
        }
        return balanceByIban.subtract(transferAmount);

    }

    private void blinkTaxDeduction(BigDecimal transferAmount) {
        if (transferAmount.compareTo(new BigDecimal("999.00")) > 0) {
            BigDecimal blinkTax = transferAmount.multiply(new BigDecimal("0.10"))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            blikTaxService.applyTax(blinkTax);
        }
    }

    private void bankFee(BigDecimal transferAmount) {
        BigDecimal bankTransferFee = transferAmount.multiply(new BigDecimal("0.05"))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
        catBankFeeService.transferFee(bankTransferFee);
    }

}
