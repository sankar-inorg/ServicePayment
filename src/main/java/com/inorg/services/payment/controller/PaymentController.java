package com.inorg.services.payment.controller;

import com.inorg.services.payment.models.paypal.PaypalAuthRequest;
import com.inorg.services.payment.models.paypal.PaypalPaymentTokenRequest;
import com.inorg.services.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Validated
@RequestMapping(path = "/payment", produces = APPLICATION_JSON_VALUE)
public class PaymentController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Get Paypal token
     *
     * @param customerId
     * @return PayPal Client Token
     */
    @GetMapping(value = "/paypal/token", produces = APPLICATION_JSON_VALUE)
    public String createPaypalToken(
            @RequestParam(required = false)  String customerId) {
        LOG.info("createPaypalToken invoked");
        return paymentService.getPaypalToken(customerId);
    }

    /**
     * Add PayPal Payment Method
     *
     * @param paymentTokenRequest
     * @return Payment Method Token
     */
    @PostMapping(value = "/paypal/paymentMethod", produces = APPLICATION_JSON_VALUE)
    public String createPaypalPaymentMethod(
            @RequestBody @Valid PaypalPaymentTokenRequest paymentTokenRequest) {
        LOG.info("createPaypalPaymentMethod invoked");
        return paymentService.addPaypalPaymentMethod(paymentTokenRequest);
    }

    /**
     * Authorize PayPal Payment
     *
     * @param paypalAuthRequest
     * @return Payment Authorization Transaction ID
     */
    @PostMapping(value = "/paypal/authorize", produces = APPLICATION_JSON_VALUE)
    public String authorizePaypalPayment(
            @RequestBody @Valid PaypalAuthRequest paypalAuthRequest) {
        LOG.info("authorizePaypalPayment invoked");
        return paymentService.authorizePaypalPayment(paypalAuthRequest.getPaymentMethodToken(), paypalAuthRequest.getAmount());
    }
}
