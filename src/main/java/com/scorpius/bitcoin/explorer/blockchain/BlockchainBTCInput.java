package com.scorpius.bitcoin.explorer.blockchain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.bitcoin.explorer.BTCInput;
import java.util.Map;
import lombok.ToString;

@ToString()
public class BlockchainBTCInput extends BTCInput {

    @JsonAlias("addr")
    @JsonProperty(required = true)
    private String address;

    @JsonAlias("value")
    @JsonProperty(required = true)
    private long satoshis;

    @JsonAlias("prev_out")
    @JsonProperty(required = true)
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
