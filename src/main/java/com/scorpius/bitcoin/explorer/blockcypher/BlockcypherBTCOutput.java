package com.scorpius.bitcoin.explorer.blockcypher;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.bitcoin.explorer.BTCOutput;
import lombok.ToString;

@ToString()
public class BlockcypherBTCOutput extends BTCOutput {

    @JsonAlias("addr")
    @JsonProperty(required = true)
    private String address;

    @JsonAlias("value")
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
