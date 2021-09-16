package io.github.bitscorpio.bitcoin;

import io.github.bitscorpio.bitcoin.impl.BlockchainBTCExplorer;
import io.github.bitscorpio.bitcoin.impl.BlockcypherBTCExplorer;
import io.github.bitscorpio.bitcoin.record.BTCAddress;
import io.github.bitscorpio.bitcoin.record.BTCTransaction;
import java.time.Duration;
import java.util.Objects;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

public class MultiSourceBTCExplorer extends MultiRequestBTCExplorer {

    private final RateLimitedBTCExplorer[] explorers;
    private final RetryPolicy<Object> retryPolicy;

    public static MultiSourceBTCExplorer createDefault() {
        return new MultiSourceBTCExplorer(new BlockchainBTCExplorer(), new BlockcypherBTCExplorer());
    }

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
