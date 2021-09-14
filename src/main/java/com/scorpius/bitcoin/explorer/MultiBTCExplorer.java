package com.scorpius.bitcoin.explorer;

import com.scorpius.bitcoin.Constants;
import com.scorpius.bitcoin.RateLimitAvoider;
import com.scorpius.bitcoin.explorer.blockchain.BlockchainBTCExplorer;
import com.scorpius.bitcoin.explorer.blockcypher.BlockcypherBTCExplorer;
import java.time.Duration;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

/**
 * A custom implementation that utilizes multiple {@link BTCExplorer} implementations to mitigate enforced rate-limits.
 */
public class MultiBTCExplorer extends BTCExplorer {

    /**
     * The {@link BTCExplorer}s to be utilized.
     */
    private final BTCExplorer[] explorers;

    /**
     * The sleep duration before re-attempting a call when all child {@link BTCExplorer}s' {@link RateLimitAvoider}s cannot process requests.
     */
    private final Duration retrySleepDuration;

    /**
     * Creates an instance with all available {@link BTCExplorer} implementations & {@link Constants#DEFAULT_RETRY_SLEEP_DURATION} when all child {@link BTCExplorer}s' {@link RateLimitAvoider}s cannot process requests.
     */
    public MultiBTCExplorer() {
        this(Constants.DEFAULT_RETRY_SLEEP_DURATION, new BlockchainBTCExplorer(), new BlockcypherBTCExplorer());
    }

    /**
     * Creates an instance with selection of {@link BTCExplorer} implementations & {@link Constants#DEFAULT_RETRY_SLEEP_DURATION} when all child {@link BTCExplorer}s' {@link RateLimitAvoider}s cannot process requests.
     * @param explorers child {@link BTCExplorer}s to be utilized.
     */
    public MultiBTCExplorer(BTCExplorer... explorers) {
        this(Constants.DEFAULT_RETRY_SLEEP_DURATION, explorers);
    }

    /**
     *
     * @param retrySleepDuration The sleep duration before re-attempting a call when all child {@link BTCExplorer}s' {@link RateLimitAvoider}s cannot process requests.
     * @param explorers child {@link BTCExplorer}s to be utilized.
     */
    public MultiBTCExplorer(Duration retrySleepDuration, BTCExplorer... explorers) {
        super(null);
        this.explorers = explorers;
        this.retrySleepDuration = retrySleepDuration;
    }

    /**
     * Utilizes the first available child {@link BTCExplorer} to make the API request.
     * Refer to documentation at {@link BTCExplorer#getAddress(String)} and {@link BTCExplorer#getAddressWithCombinedTransactions(String, BTCAddress)}
     */
    @Override
    protected BTCAddress getAddressWithCombinedTransactions(String address, @Nullable BTCAddress existingAddress) throws Exception {
        for (BTCExplorer explorer : explorers) {
            if (explorer.getRateLimitAvoider() == null || explorer.getRateLimitAvoider().canProcess()) {
                return explorer.getAddressWithCombinedTransactions(address, existingAddress);
            }
        }
        Thread.sleep(retrySleepDuration.toMillis());
        return getAddressWithCombinedTransactions(address, existingAddress);
    }

    /**
     * Retrieves a transaction by its hash.
     * @param hash Transaction hash.
     * @return The requested {@link BTCTransaction} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by any of the child {@link BTCExplorer}s' {@link RateLimitAvoider#process(Callable)}.
     */
    @Override
    public BTCTransaction getTransaction(String hash) throws Exception {
        for (BTCExplorer explorer : explorers) {
            if (explorer.getRateLimitAvoider() == null || explorer.getRateLimitAvoider().canProcess()) {
                return explorer.getTransaction(hash);
            }
        }
        Thread.sleep(retrySleepDuration.toMillis());
        return getTransaction(hash);
    }
}
