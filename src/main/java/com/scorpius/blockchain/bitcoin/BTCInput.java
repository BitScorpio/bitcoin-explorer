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

    private String address;

    private long value;

    private boolean spent;

    @JsonProperty("prev_out")
    @SuppressWarnings("unused")
    private void unpackNested(Map<String, Object> input) {
        this.address = (String) input.get("addr");
        this.value = ((Integer) input.get("value")).longValue();
        this.spent = (boolean) input.get("spent");
    }
}
