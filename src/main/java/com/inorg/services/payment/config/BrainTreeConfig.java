package com.inorg.services.payment.config;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.braintree")
@Data
public class BrainTreeConfig {
    private String merchantId;
    private String publicKey;
    private String privateKey;

    @Bean(name = "BrainTreeGateway")
    public BraintreeGateway connectBrainTreeGateway() {
        return new BraintreeGateway(Environment.SANDBOX,
                this.getMerchantId(),
                this.getPublicKey(),
                this.getPrivateKey());
    }
}
