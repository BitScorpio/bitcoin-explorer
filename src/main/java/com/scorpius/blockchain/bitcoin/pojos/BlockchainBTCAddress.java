package com.scorpius.blockchain.bitcoin.pojos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.ToString;

@ToString
public class BlockchainBTCAddress implements BTCAddress {

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
    private List<BlockchainBTCTransaction> transactions;

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public long getSent() {
        return sent;
    }

    @Override
    public long getReceived() {
        return received;
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
