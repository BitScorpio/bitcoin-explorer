package com.scorpius.explorer.bitcoin.impl;

import com.scorpius.explorer.bitcoin.MultiRequestBTCExplorer;
import com.scorpius.explorer.bitcoin.RateLimitedBTCExplorer;
import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;
import javax.annotation.Nullable;

public class MultiBTCExplorer implements MultiRequestBTCExplorer {

    private final RateLimitedBTCExplorer[] explorers;

    public MultiBTCExplorer(RateLimitedBTCExplorer... rateLimitedBTCExplorers) {
        this.explorers = rateLimitedBTCExplorers;
    }

    @Override
    public BTCAddress getAddressCombineTransactions(String address, @Nullable BTCAddress existingAddress) throws Exception {
        for (RateLimitedBTCExplorer explorer : explorers) {
            if (explorer.getRateLimitAvoider() == null || explorer.getRateLimitAvoider().canProcess()) {
                return explorer.getAddressCombineTransactions(address, existingAddress);
            }
        }
        Thread.sleep(200);
        return getAddressCombineTransactions(address, existingAddress);
    }

    @Override
    public BTCTransaction getTransaction(String hash) throws Exception {
        for (RateLimitedBTCExplorer explorer : explorers) {
            if (explorer.getRateLimitAvoider() == null || explorer.getRateLimitAvoider().canProcess()) {
                return explorer.getTransaction(hash);
            }
        }
        Thread.sleep(200);
        return getTransaction(hash);
    }
}
