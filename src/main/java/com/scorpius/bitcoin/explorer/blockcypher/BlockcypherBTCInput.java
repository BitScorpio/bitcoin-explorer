package com.scorpius.bitcoin.explorer.blockcypher;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.bitcoin.explorer.BTCInput;
import lombok.ToString;

@ToString()
public class BlockcypherBTCInput extends BTCInput {

    @JsonAlias("address")
    @JsonProperty(required = true)
    private String address;

    @JsonAlias("output_value")
    @JsonProperty(required = true)
    private long satoshis;

    @JsonAlias("addresses")
    @JsonProperty(required = true)
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
