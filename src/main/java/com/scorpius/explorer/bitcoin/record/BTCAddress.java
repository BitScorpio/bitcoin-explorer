package com.scorpius.explorer.bitcoin.record;

import java.util.List;

public record BTCAddress(String hash, long balance, long transactionsCount, List<BTCTransaction> transactions) {

    public void combineTransactions(BTCAddress address) {
        if (!hash().equals(address.hash())) {
            throw new RuntimeException("Address hashes do not match");
        }
        transactions().addAll(address.transactions());
    }
}
