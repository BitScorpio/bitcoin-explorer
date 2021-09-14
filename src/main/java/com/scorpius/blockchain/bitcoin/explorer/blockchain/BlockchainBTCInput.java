package com.scorpius.blockchain.bitcoin.explorer.blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.blockchain.bitcoin.explorer.BTCInput;
import java.util.Map;
import lombok.ToString;

@ToString()
public class BlockchainBTCInput extends BTCInput {

    @JsonProperty("addr")
    private String address;

    @JsonProperty("value")
    private long satoshis;

    @JsonProperty("prev_out")
    @SuppressWarnings("unused")
    private void unpackNested(Map<String, Object> input) {
        this.address = (String) input.get("addr");
        this.satoshis = ((Number) input.get("value")).longValue();
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