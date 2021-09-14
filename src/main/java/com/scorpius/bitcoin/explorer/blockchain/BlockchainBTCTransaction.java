package com.scorpius.bitcoin.explorer.blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.bitcoin.explorer.BTCInput;
import com.scorpius.bitcoin.explorer.BTCOutput;
import com.scorpius.bitcoin.explorer.BTCTransaction;
import lombok.ToString;

@ToString()
public class BlockchainBTCTransaction extends BTCTransaction {

    @JsonProperty("hash")
    private String hash;

    @JsonProperty(value = "block_height")
    private Long blockHeight;

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
