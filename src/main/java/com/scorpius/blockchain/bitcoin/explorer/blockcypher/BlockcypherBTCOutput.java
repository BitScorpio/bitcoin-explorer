package com.scorpius.blockchain.bitcoin.explorer.blockcypher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.blockchain.bitcoin.explorer.BTCOutput;
import lombok.ToString;

@ToString()
public class BlockcypherBTCOutput extends BTCOutput {

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
