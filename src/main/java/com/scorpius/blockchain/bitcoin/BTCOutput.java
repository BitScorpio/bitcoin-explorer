package com.scorpius.blockchain.bitcoin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString()
@JsonIgnoreProperties(ignoreUnknown = true)
public class BTCOutput {

    @JsonProperty("addr")
    private String address;

    @JsonProperty("value")
    private long value;

    @JsonProperty("spent")
    private boolean spent;
}
