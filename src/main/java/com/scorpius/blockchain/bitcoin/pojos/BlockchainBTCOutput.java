package com.scorpius.blockchain.bitcoin.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString()
public class BlockchainBTCOutput implements BTCOutput {

    @JsonProperty("addr")
    private String address;

    @JsonProperty("value")
    private long satoshis;

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public long getSatoshis() {
        return satoshis;
    }
}
