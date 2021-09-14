package com.scorpius.bitcoin.explorer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BTCAddress {

    public abstract String getHash();

    public abstract long getBalance();

    public abstract long getTransactionsCount();

    public abstract List<BTCTransaction> getTransactions();

    public void combineTransactions(BTCAddress address) {
        if (!getHash().equals(address.getHash())) {
            throw new RuntimeException("Address hashes do not match");
        }
        getTransactions().addAll(address.getTransactions());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BTCAddress)) {
            return false;
        }
        BTCAddress address = (BTCAddress) obj;
        return address.getHash().equals(getHash())
               && address.getBalance() == getBalance()
               && address.getTransactionsCount() == getTransactionsCount()
               && address.getTransactions().equals(getTransactions());
    }
}
