package com.scorpius.blockchain.bitcoin.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString()
public class BlockcipherBTCInput implements BTCInput {

    @JsonProperty("address")
    private String address;

    @JsonProperty("output_value")
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
