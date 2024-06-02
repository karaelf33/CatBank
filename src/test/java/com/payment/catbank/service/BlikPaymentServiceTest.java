package com.payment.catbank.service;

import com.payment.catbank.exception.UnsupportedPaymentTypeException;
import com.payment.catbank.model.PaymentRequest;
import com.payment.catbank.repo.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlikPaymentServiceTest {


    // call blik tax service-10%- done
    // call cat-bank deduction service-5% -done
    // deduction sent amount from sender account
    // add sent amount to receiver account

    @Mock
    private BlikTaxService blikTaxService;

    @Mock
    private CatBankFeeService catBankFeeService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private BlikPaymentService underTest;

    @Test
    void whenPaymentIsBlinkAndAmountHigher999ThenApply10PercentBlikTaxAnd5PercentBankTransferFee() {
        var request = PaymentRequest.builder()
                .paymentType("BLIK")
                .amount(new BigDecimal("1000.00"))
                .receiverIban("DE12345678901234567890")
                .senderIban("DE09876543210987654321")
                .build();

        when(userAccountRepository.findBalanceByIban(request.getSenderIban()))
                .thenReturn(new BigDecimal("10000.00"));

        underTest.paymentWithBlik(request);

        verify(blikTaxService, times(1))
                .applyTax(new BigDecimal("100.00"));
        verify(catBankFeeService, times(1))
                .transferFee(new BigDecimal("50.00"));
    }

    @Test
    void whenPaymentIsSmallerThen999ThenApplyOnly5PercentBankFee() {
        var request = PaymentRequest.builder()
                .paymentType("BLIK")
                .amount(new BigDecimal("100.00"))
                .receiverIban("DE12345678901234567890")
                .senderIban("DE09876543210987654321")
                .build();

        when(userAccountRepository.findBalanceByIban(request.getSenderIban()))
                .thenReturn(new BigDecimal("10000.00"));

        underTest.paymentWithBlik(request);

        verifyNoInteractions(blikTaxService);
    }

    @Test
    void whenPaymentTypeIsNotBlinkThenThrowException() {
        var request = PaymentRequest.builder()
                .paymentType("another payment type")
                .amount(new BigDecimal("1000.00"))
                .receiverIban("DE12345678901234567890")
                .senderIban("DE09876543210987654321")
                .build();

        UnsupportedPaymentTypeException exception = assertThrows(UnsupportedPaymentTypeException.class,
                () -> underTest.paymentWithBlik(request));

        assertThat(exception.getMessage()).isEqualTo("Please choose Blik payment type");

        verifyNoInteractions(blikTaxService, catBankFeeService);
    }

    @Test
    void whenPaymentSendThenReturnRemainingAccountBalanceOfSender() {
        var request = PaymentRequest.builder()
                .paymentType("BLIK")
                .amount(new BigDecimal("1000.00"))
                .receiverIban("iban of receiver")
                .senderIban("iban of sender")
                .build();

        when(userAccountRepository.findBalanceByIban(request.getSenderIban()))
                .thenReturn(new BigDecimal("10000.00"));

        BigDecimal expected = underTest.paymentWithBlik(request);

        assertThat(expected).isEqualTo(new BigDecimal("9000.00"));
    }

    @Test
    void whenUserTransferCountIsHigher99ThenAwardUserWith100(){
        var request = PaymentRequest.builder()
                .paymentType("BLIK")
                .amount(new BigDecimal("1000.00"))
                .receiverIban("iban of receiver")
                .senderIban("iban of sender")
                .build();

        String senderIban = request.getSenderIban();
        when(userAccountRepository.findBalanceByIban(senderIban))
                .thenReturn(new BigDecimal("10000.00"));
        when(userAccountRepository.findTransferCountByIban(senderIban))
                .thenReturn(100);

        BigDecimal actual = underTest.paymentWithBlik(request);
        BigDecimal expected = new BigDecimal("9100.00");

        assertThat(actual).isEqualTo(expected);
    }
}