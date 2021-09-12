package com.scorpius.blockchain.bitcoin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString()
@JsonIgnoreProperties(ignoreUnknown = true)
public class BTCInput {

    @JsonProperty("addr")
    private String address;

    @JsonProperty("value")
    private long satoshis;

    @JsonProperty("spent")
    private boolean spent;

    @JsonProperty("prev_out")
    @SuppressWarnings("unused")
    private void unpackNested(Map<String, Object> input) {
        this.address = (String) input.get("addr");
        this.satoshis = ((Integer) input.get("value")).longValue();
        this.spent = (boolean) input.get("spent");
    }
}
