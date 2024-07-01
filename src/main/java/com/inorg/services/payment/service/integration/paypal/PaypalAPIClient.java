package com.inorg.services.payment.service.integration.paypal;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.ClientTokenRequest;
import com.braintreegateway.Customer;
import com.braintreegateway.CustomerRequest;
import com.braintreegateway.CustomerSearchRequest;
import com.braintreegateway.PaymentMethod;
import com.braintreegateway.PaymentMethodRequest;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PaypalAPIClient {
    private static final Logger LOG = LoggerFactory.getLogger(PaypalAPIClient.class);

    private final BraintreeGateway braintreeGateway;

    public PaypalAPIClient(BraintreeGateway braintreeGateway) {
        this.braintreeGateway = braintreeGateway;
    }

    public String getClientToken(String customerId) {
        LOG.info("Get Paypal Client for Customer : {}", customerId);
        ClientTokenRequest clientTokenRequest = new ClientTokenRequest();
        if(StringUtils.hasText(customerId)) {
            clientTokenRequest.customerId(customerId);
        }
        return this.braintreeGateway.clientToken().generate(clientTokenRequest);
    }

    public String addPaymentMethod(String customerEmail, String nonce, String deviceData) {

        String paymentMethodToken = null;

        LOG.info("Add Paypal Payment  for customer Email : {}", customerEmail);

        List<String> customerIds = this.braintreeGateway.customer()
                .search(new CustomerSearchRequest().email().is(customerEmail))
                .getIds();

        String customerId = null;
        if(customerIds.isEmpty()) {
            LOG.info("No Paypal Customers found with email : {}", customerEmail);
            CustomerRequest customerRequest = new CustomerRequest().email(customerEmail);
            Result<Customer> createCustomerResult = this.braintreeGateway.customer()
                    .create(customerRequest);

            if(createCustomerResult.isSuccess()) {
                LOG.info("New Paypal Customers created with email : {}", customerEmail);
                customerId = createCustomerResult.getTarget().getId();
            } else {
                LOG.error("Could not Create Paypal Customers with email : {}", customerEmail);
            }
        } else {
            customerId = customerIds.get(0);
            LOG.info("Paypal Customers matching to email : {} are : {}", customerEmail, customerId);
        }


        if(StringUtils.hasText(customerId)) {
            PaymentMethodRequest paymentMethodRequest = new PaymentMethodRequest()
                    .paymentMethodNonce(nonce)
                    .deviceData(deviceData)
                    .customerId(customerId);
            Result<? extends PaymentMethod> paymentMethodResult = this.braintreeGateway.paymentMethod()
                    .create(paymentMethodRequest);

            if(paymentMethodResult.isSuccess()) {
                PaymentMethod paymentMethod = paymentMethodResult.getTarget();
                LOG.info("Successfully created paypal Payment method : {}", paymentMethod);
                paymentMethodToken = paymentMethod.getToken();
            } else {
                LOG.error("Could not create paypal Payment method for customer : {}", customerEmail);
                if (paymentMethodResult.getErrors().size() > 0) {
                    LOG.error("Error : {}", paymentMethodResult.getErrors().getAllValidationErrors().stream()
                            .map(ValidationError::getMessage).collect(Collectors.joining(", ")));
                } else {
                    LOG.error("Error : {}", paymentMethodResult.getMessage());
                }
            }
        }

        return paymentMethodToken;
    }


    public String authPaypalPayment(String paymentMethodToken, BigDecimal amount) {
        String transactionId = null;
        LOG.info("Auth Paypal Payment for paymentMethodToken : {} and amount : {}", paymentMethodToken, amount);
        Result<Transaction> result = this.braintreeGateway.transaction().sale(new TransactionRequest()
                .amount(amount)
                .paymentMethodToken(paymentMethodToken)
                .options()
                .submitForSettlement(false)
                .done());
        if(result.isSuccess()) {
            LOG.info("Successfully Auth Paypal Payment for paymentMethodToken : {} and amount : {}", paymentMethodToken, amount);
            transactionId = result.getTarget().getId();
        } else {
            LOG.error("Could not Auth Paypal Payment for paymentMethodToken : {} and amount : {}", paymentMethodToken, amount);
            if (result.getErrors().size() > 0) {
                LOG.error("Error : {}", result.getErrors().getAllValidationErrors().stream()
                        .map(ValidationError::getMessage).collect(Collectors.joining(", ")));
            } else {
                LOG.error("Error : {}", result.getMessage());
            }
        }

        return transactionId;
    }
}
