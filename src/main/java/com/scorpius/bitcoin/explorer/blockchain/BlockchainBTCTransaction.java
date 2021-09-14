package com.scorpius.bitcoin.explorer.blockchain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.bitcoin.explorer.BTCInput;
import com.scorpius.bitcoin.explorer.BTCOutput;
import com.scorpius.bitcoin.explorer.BTCTransaction;
import lombok.ToString;

@ToString()
public class BlockchainBTCTransaction extends BTCTransaction {

    @JsonAlias("hash")
    @JsonProperty(required = true)
    private String hash;

    @JsonAlias(value = "block_height")
    @JsonProperty(required = true)
    private Long blockHeight;

    @JsonAlias("fee")
    @JsonProperty(required = true)
    private long fee;

    @JsonAlias("vin_sz")
    @JsonProperty(required = true)
    private int inputsCount;

    @JsonAlias("vout_sz")
    @JsonProperty(required = true)
    private int outputsCount;

    @JsonAlias("inputs")
    @JsonProperty(required = true)
    private BlockchainBTCInput[] inputs;

    @JsonAlias("out")
    @JsonProperty(required = true)
    private BlockchainBTCOutput[] outputs;

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public long getBlockHeight() {
        return blockHeight == null ? -1 : blockHeight;
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
