package com.inorg.services.payment.service;

import com.commercetools.api.client.ProjectApiRoot;
import com.inorg.services.payment.models.paypal.PaypalPaymentTokenRequest;
import com.inorg.services.payment.service.integration.paypal.PaypalAPIClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final ProjectApiRoot apiRoot;
    private final PaypalAPIClient paypalAPIClient;


    public PaymentServiceImpl(ProjectApiRoot apiRoot, PaypalAPIClient paypalAPIClient) {
        this.apiRoot = apiRoot;
        this.paypalAPIClient = paypalAPIClient;
    }


    @Override
    public String getPaypalToken(String customerId) {
        return this.paypalAPIClient.getClientToken(customerId);
    }

    @Override
    public String addPaypalPaymentMethod(PaypalPaymentTokenRequest paymentTokenRequest) {
        return this.paypalAPIClient.addPaymentMethod(paymentTokenRequest.getEmail(), paymentTokenRequest.getNonce(), paymentTokenRequest.getDeviceData());
    }

    @Override
    public String authorizePaypalPayment(String paymentMethodToken, BigDecimal amount) {
        return this.paypalAPIClient.authPaypalPayment(paymentMethodToken, amount);
    }
}
