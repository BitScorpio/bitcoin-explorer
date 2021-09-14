package com.scorpius.blockchain.bitcoin.explorer.blockcypher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.blockchain.bitcoin.explorer.BTCInput;
import com.scorpius.blockchain.bitcoin.explorer.BTCOutput;
import com.scorpius.blockchain.bitcoin.explorer.BTCTransaction;
import lombok.ToString;

@ToString()
public class BlockcypherBTCTransaction extends BTCTransaction {

    @JsonProperty("hash")
    private String hash;

    @JsonProperty("block_height")
    private long blockHeight;

    @JsonProperty("fees")
    private long fee;

    @JsonProperty("vin_sz")
    private int inputsCount;

    @JsonProperty("vout_sz")
    private int outputsCount;

    @JsonProperty("inputs")
    private BlockcypherBTCInput[] inputs;

    @JsonProperty("outputs")
    private BlockcypherBTCOutput[] outputs;

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public long getBlockHeight() {
        return blockHeight;
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
