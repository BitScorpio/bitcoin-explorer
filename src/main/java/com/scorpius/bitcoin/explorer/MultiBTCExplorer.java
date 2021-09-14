package com.scorpius.bitcoin.explorer;

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
     * The sleep duration before re-attempting a call when all child {@link BTCExplorer}s' {@link com.scorpius.bitcoin.explorer.RateLimitAvoider}s cannot process requests.
     */
    private final Duration timeout;

    /**
     * Creates an instance with all available {@link BTCExplorer} implementations & 1 millisecond timeout when all child {@link BTCExplorer}s' {@link com.scorpius.bitcoin.explorer.RateLimitAvoider}s cannot process requests.
     */
    public MultiBTCExplorer() {
        this(Duration.ofMillis(1), new BlockchainBTCExplorer(), new BlockcypherBTCExplorer());
    }

    /**
     * Creates an instance with selection of {@link BTCExplorer} implementations & 1 millisecond timeout when all child {@link BTCExplorer}s' {@link com.scorpius.bitcoin.explorer.RateLimitAvoider}s cannot process requests.
     * @param explorers child {@link BTCExplorer}s to be utilized.
     */
    public MultiBTCExplorer(BTCExplorer... explorers) {
        this(Duration.ofSeconds(1), explorers);
    }

    /**
     *
     * @param timeout The sleep duration before re-attempting a call when all child {@link BTCExplorer}s' {@link com.scorpius.bitcoin.explorer.RateLimitAvoider}s cannot process requests.
     * @param explorers child {@link BTCExplorer}s to be utilized.
     */
    public MultiBTCExplorer(Duration timeout, BTCExplorer... explorers) {
        super(null);
        this.explorers = explorers;
        this.timeout = timeout;
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
        Thread.sleep(timeout.toMillis());
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
        Thread.sleep(timeout.toMillis());
        return getTransaction(hash);
    }
}
