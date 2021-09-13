package com.scorpius.blockchain.bitcoin.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString()
public class BlockcipherBTCOutput implements BTCOutput {

    @JsonProperty("addr")
    private String address;

    @JsonProperty("value")
    private long satoshis;

    @JsonProperty("addresses")
    @SuppressWarnings("unused")
    private void unpackNested(Object[] addresses) {
        this.address = (String) addresses[0];
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public long getSatoshis() {
        return satoshis;
    }
}
