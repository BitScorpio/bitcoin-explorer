package com.scorpius.blockchain.bitcoin;

import dev.yasper.rump.Rump;
import dev.yasper.rump.response.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.Setter;

/**
 * An API implementation for the <a href="https://www.blockchain.com/api">Blockchain Explorer API</a>.
 */
public class BTCExplorer {

    private static final String BASE = "https://blockchain.info/";
    private static final String SINGLE_TRANSACTION = BASE + "rawtx/";
    private static final String SINGLE_ADDRESS = BASE + "rawaddr/";

    /**
     * Used to ensure that only one threads is making an HTTP request.
     */
    private final Lock lock;

    /**
     * The duration between two consecutive HTTP requests, 5 seconds by default.
     */
    @Getter
    @Setter
    private Duration durationPerRequest;

    /**
     * The last HTTP request instant.
     */
    private Instant lastRequestInstant;

    public BTCExplorer() {
        this.lock = new ReentrantLock(true);
        this.durationPerRequest = Duration.ofSeconds(5);
        this.lastRequestInstant = Instant.now().minus(durationPerRequest);
    }

    /**
     * Retrieves an address and some corresponding data, see {@link BTCAddress} for specifics.
     * @param address Address or Hash160.
     * @return The requested {@link BTCAddress} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link #honorRateLimit(Callable)}.
     */
    // TODO: Get all transactions, currently limited to 50 by blockchain.info
    public BTCAddress getAddress(String address) throws Exception {
        return honorRateLimit(() -> Rump.get(SINGLE_ADDRESS + address, BTCAddress.class)).getBody();
    }

    /**
     * Retrieves a transaction by its hash.
     * @param hash Transaction hash.
     * @return The requested {@link BTCTransaction} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link #honorRateLimit(Callable)}.
     */
    public BTCTransaction getTransaction(String hash) throws Exception {
        return honorRateLimit(() -> Rump.get(SINGLE_TRANSACTION + hash, BTCTransaction.class)).getBody();
    }

    /**
     * All HTTP API calls should go through this method to avoid being rate-limited.
     * @param callable The {@link Callable} that should be used to return a value.
     * @param <T> Type parameter of the {@code callable} parameter.
     * @return Object returned by the {@code callable} parameter.
     * @throws Exception {@link InterruptedException} if the thread gets interrupted as well as any exceptions thrown inside the {@code callable} parameter.
     */
    private <T extends HttpResponse<?>> T honorRateLimit(Callable<T> callable) throws Exception {
        if (lastRequestInstant.plus(durationPerRequest).isBefore(Instant.now())) {
            try {
                lock.lockInterruptibly();
                return callable.call();
            } finally {
                lastRequestInstant = Instant.now();
                lock.unlock();
            }
        } else {
            Thread.sleep(1);
            return honorRateLimit(callable);
        }
    }
}
