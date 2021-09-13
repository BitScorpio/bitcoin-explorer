package com.scorpius.blockchain.bitcoin.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface BTCTransaction {

    String getHash();

    long getFee();

    int getInputsCount();

    int getOutputsCount();

    BTCInput[] getInputs();

    BTCOutput[] getOutputs();
}
