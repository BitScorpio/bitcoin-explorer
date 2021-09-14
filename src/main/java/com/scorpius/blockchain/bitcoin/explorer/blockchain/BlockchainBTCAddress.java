package com.scorpius.blockchain.bitcoin.explorer.blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scorpius.blockchain.bitcoin.explorer.BTCAddress;
import com.scorpius.blockchain.bitcoin.explorer.BTCTransaction;
import java.util.List;
import lombok.ToString;

@ToString
public class BlockchainBTCAddress extends BTCAddress {

    @JsonProperty("address")
    private String hash;

    @JsonProperty("final_balance")
    private long balance;

    @JsonProperty("n_tx")
    private long transactionsCount;

    @JsonProperty("txs")
    private List<BlockchainBTCTransaction> transactions;

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
