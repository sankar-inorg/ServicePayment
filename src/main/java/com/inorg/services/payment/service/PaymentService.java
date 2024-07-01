package com.inorg.services.payment.service;

import com.inorg.services.payment.models.paypal.PaypalPaymentTokenRequest;

import java.math.BigDecimal;

public interface PaymentService {

    String getPaypalToken(String customerId);
    String addPaypalPaymentMethod(PaypalPaymentTokenRequest paymentTokenRequest);
    String authorizePaypalPayment(String paymentMethodToken, BigDecimal amount);
}
