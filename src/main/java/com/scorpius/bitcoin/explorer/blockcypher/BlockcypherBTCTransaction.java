package com.scorpius.bitcoin.explorer.blockcypher;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.bitcoin.explorer.BTCInput;
import com.scorpius.bitcoin.explorer.BTCOutput;
import com.scorpius.bitcoin.explorer.BTCTransaction;
import lombok.ToString;

@ToString()
public class BlockcypherBTCTransaction extends BTCTransaction {

    @JsonAlias("hash")
    @JsonProperty(required = true)
    private String hash;

    @JsonAlias("block_height")
    @JsonProperty(required = true)
    private long blockHeight;

    @JsonAlias("fees")
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
    private BlockcypherBTCInput[] inputs;

    @JsonAlias("outputs")
    @JsonProperty(required = true)
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
