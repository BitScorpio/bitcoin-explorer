package com.scorpius.bitcoin.explorer.blockcypher;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.bitcoin.explorer.BTCAddress;
import com.scorpius.bitcoin.explorer.BTCTransaction;
import java.util.List;
import lombok.ToString;

@ToString
public class BlockcypherBTCAddress extends BTCAddress {

    @JsonAlias("address")
    @JsonProperty(required = true)
    private String hash;

    @JsonAlias("final_balance")
    @JsonProperty(required = true)
    private long balance;

    @JsonAlias("final_n_tx")
    @JsonProperty(required = true)
    private long transactionsCount;

    @JsonAlias("txs")
    @JsonProperty(required = true)
    private List<BlockcypherBTCTransaction> transactions;

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public long getBalance() {
        return balance;
    }

    @Override
    public long getTransactionsCount() {
        return transactionsCount;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<BTCTransaction> getTransactions() {
        return (List<BTCTransaction>) (List<? extends BTCTransaction>) transactions;
    }
}
