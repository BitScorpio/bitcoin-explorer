package com.scorpius.blockchain.bitcoin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BTCAddress {

    @JsonProperty("address")
    private String hash;

    @JsonProperty("total_sent")
    private int sent;

    @JsonProperty("total_received")
    private int received;

    @JsonProperty("final_balance")
    private int balance;

    @JsonProperty("n_tx")
    private int transactionsCount;

    @JsonProperty("txs")
    private BTCTransaction[] transactions;
}
