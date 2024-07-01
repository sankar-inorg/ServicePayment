package com.inorg.services.payment.models.paypal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaypalPaymentTokenRequest implements Serializable {

    @NotNull
    private String email;

    @NotNull
    private String nonce;

    @NotNull
    private String deviceData;
}
