package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;
import java.time.Duration;
import java.util.Objects;
import javax.annotation.Nullable;
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
    public BTCAddress getAddressCombineTransactions(String address, @Nullable BTCAddress existingAddress) {
        return Failsafe.with(retryPolicy).get(() -> {
            for (RateLimitedBTCExplorer explorer : explorers) {
                if (explorer.getRateLimitAvoider() == null || explorer.getRateLimitAvoider().canProcess()) {
                    return explorer.getAddressCombineTransactions(address, existingAddress);
                }
            }
            return null;
        });
    }

    @Override
    public BTCTransaction getTransaction(String hash) {
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
