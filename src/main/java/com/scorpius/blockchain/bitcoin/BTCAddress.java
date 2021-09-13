package com.scorpius.blockchain.bitcoin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BTCAddress {

    @JsonProperty("address")
    private String hash;

    @JsonProperty("total_sent")
    private long sent;

    @JsonProperty("total_received")
    private long received;

    @JsonProperty("final_balance")
    private long balance;

    @JsonProperty("n_tx")
    private long transactionsCount;

    @JsonProperty("txs")
    private List<BTCTransaction> transactions;
}
