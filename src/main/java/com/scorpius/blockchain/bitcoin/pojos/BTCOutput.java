package com.scorpius.blockchain.bitcoin.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface BTCOutput {

    String getAddress();

    long getSatoshis();
}
