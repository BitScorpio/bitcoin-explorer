package com.scorpius.blockchain.bitcoin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString()
@JsonIgnoreProperties(ignoreUnknown = true)
public class BTCTransaction {

    @JsonProperty("hash")
    private String hash;

    @JsonProperty("fee")
    private long fee;

    @JsonProperty("vin_sz")
    private int inputsCount;

    @JsonProperty("vout_sz")
    private int outputsCount;

    @JsonProperty("inputs")
    private BTCInput[] inputs;

    @JsonProperty("out")
    private BTCOutput[] outputs;
}
