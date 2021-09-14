package com.scorpius.blockchain.bitcoin.explorer.blockcypher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.blockchain.bitcoin.explorer.BTCInput;
import lombok.ToString;

@ToString()
public class BlockcypherBTCInput extends BTCInput {

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
