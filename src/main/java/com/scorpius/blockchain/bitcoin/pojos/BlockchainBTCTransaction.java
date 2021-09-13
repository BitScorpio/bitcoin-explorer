package com.scorpius.blockchain.bitcoin.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString()
public class BlockchainBTCTransaction implements BTCTransaction {

    @JsonProperty("hash")
    private String hash;

    @JsonProperty("fee")
    private long fee;

    @JsonProperty("vin_sz")
    private int inputsCount;

    @JsonProperty("vout_sz")
    private int outputsCount;

    @JsonProperty("inputs")
    private BlockchainBTCInput[] inputs;

    @JsonProperty("out")
    private BlockchainBTCOutput[] outputs;

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public long getFee() {
        return fee;
    }

    @Override
    public int getInputsCount() {
        return inputsCount;
    }

    @Override
    public int getOutputsCount() {
        return outputsCount;
    }

    @Override
    public BTCInput[] getInputs() {
        return inputs;
    }

    @Override
    public BTCOutput[] getOutputs() {
        return outputs;
    }
}
