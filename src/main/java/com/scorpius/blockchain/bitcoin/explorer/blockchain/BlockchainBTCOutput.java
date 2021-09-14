package com.scorpius.blockchain.bitcoin.explorer.blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.blockchain.bitcoin.explorer.BTCOutput;
import lombok.ToString;

@ToString()
public class BlockchainBTCOutput extends BTCOutput {

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
