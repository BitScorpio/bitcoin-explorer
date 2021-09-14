package com.scorpius.bitcoin.explorer.blockchain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.bitcoin.explorer.BTCOutput;
import lombok.ToString;

@ToString()
public class BlockchainBTCOutput extends BTCOutput {

    @JsonAlias("addr")
    @JsonProperty(required = true)
    private String address;

    @JsonAlias("value")
    @JsonProperty(required = true)
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
