package com.scorpius.blockchain.bitcoin.pojos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public interface BTCAddress {

    String getHash();

    long getSent();

    long getReceived();

    long getBalance();

    long getTransactionsCount();

    List<BTCTransaction> getTransactions();
}
