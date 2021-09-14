package com.scorpius.bitcoin.explorer.blockcypher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.bitcoin.explorer.BTCAddress;
import com.scorpius.bitcoin.explorer.BTCTransaction;
import java.util.List;
import lombok.ToString;

@ToString
public class BlockcypherBTCAddress extends BTCAddress {

    @JsonProperty("address")
    private String hash;

    @ToString.Exclude
    @JsonProperty("unconfirmed_balance")
    private long unconfirmedBalance;

    @JsonProperty("final_balance")
    private long balance;

    @JsonProperty("final_n_tx")
    private long transactionsCount;

    @JsonProperty("txs")
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
