package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;
import java.time.Duration;
import java.util.Objects;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

public class MultiSourceBTCExplorer extends MultiRequestBTCExplorer {

    private final RateLimitedBTCExplorer[] explorers;
    private final RetryPolicy<Object> retryPolicy;

    public MultiSourceBTCExplorer(RateLimitedBTCExplorer... rateLimitedBTCExplorers) {
        this.explorers = rateLimitedBTCExplorers;
        this.retryPolicy = new RetryPolicy<>().handleResult(null)
                                              .withMaxRetries(-1)
                                              .abortOn(Objects::nonNull)
                                              .withDelay(Duration.ofMillis(200));
    }

    @Override
    protected BTCAddress getAddressLatestTransactions(String address) {
        return Failsafe.with(retryPolicy).get(() -> {
            for (RateLimitedBTCExplorer explorer : explorers) {
                if (explorer.getRateLimitAvoider() == null || explorer.getRateLimitAvoider().canProcess()) {
                    return explorer.getAddressLatestTransactions(address);
                }
            }
            return null;
        });
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    protected BTCAddress getAddressNextTransactionsBatch(BTCAddress existingAddress) throws Exception {
        return Failsafe.with(retryPolicy).get(() -> {
            for (RateLimitedBTCExplorer explorer : explorers) {
                if (explorer.getRateLimitAvoider() == null || explorer.getRateLimitAvoider().canProcess()) {
                    return explorer.getAddressNextTransactionsBatch(existingAddress);
                }
            }
            return null;
        });
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public BTCTransaction getTransaction(String hash) throws Exception {
        return Failsafe.with(retryPolicy).get(() -> {
            for (RateLimitedBTCExplorer explorer : explorers) {
                if (explorer.getRateLimitAvoider() == null || explorer.getRateLimitAvoider().canProcess()) {
                    return explorer.getTransaction(hash);
                }
            }
            return null;
        });
    }
}
