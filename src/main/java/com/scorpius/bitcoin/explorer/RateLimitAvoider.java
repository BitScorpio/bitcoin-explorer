package com.scorpius.bitcoin.explorer;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.Setter;

/**
 * Regulates code execution to avoid rate-limits
 */
public class RateLimitAvoider {

    /**
     * Used to ensure that only one threads is using the avoider.
     */
    private final Lock lock;

    /**
     * The duration between two consecutive calls.
     */
    @Getter
    @Setter
    private Duration durationPerCall;

    /**
     * The sleep duration before re-attempting a call when {@link #durationPerCall} is violated.
     */
    @Getter
    @Setter
    private Duration timeout;

    /**
     * The last call instant.
     */
    private Instant lastCallInstant;

    /**
     * @param durationPerCall The allowed duration between two consecutive calls
     * @param timeout The sleep duration before re-attempting a call when {@link #durationPerCall} is violated.
     */
    public RateLimitAvoider(Duration durationPerCall, Duration timeout) {
        this.lock = new ReentrantLock(true);
        this.durationPerCall = durationPerCall;
        this.timeout = timeout;
        this.lastCallInstant = Instant.now().minus(durationPerCall);
    }

    /**
     * Checks if a request can be immediately processed without triggering a rate-limit.
     */
    public boolean canProcess() {
        return lastCallInstant.plus(durationPerCall).isBefore(Instant.now());
    }

    /**
     * Calls should go through this method to avoid being rate-limited.
     * @param callable The {@link Callable} that should be used to return a value.
     * @param <T> Type parameter of the {@code callable} parameter.
     * @return Object returned by the {@code callable} parameter.
     * @throws Exception {@link InterruptedException} if the thread gets interrupted as well as any exceptions thrown inside the {@code callable} parameter.
     */
    public <T> T process(Callable<T> callable) throws Exception {
        if (canProcess()) {
            try {
                lock.lockInterruptibly();
                return callable.call();
            } finally {
                lastCallInstant = Instant.now();
                lock.unlock();
            }
        } else {
            Thread.sleep(timeout.toMillis());
            return process(callable);
        }
    }
}