package com.scorpius.explorer;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

/**
 * Regulates code execution to avoid rate-limits
 */
public class RateLimitAvoider {

    private final ReentrantLock lock;
    private final Duration timeBetweenCalls;
    private final RetryPolicy<Object> retryPolicy;

    private Instant lastCallTime;

    /**
     * Creates an instance that regulates code execution according to the given parameters.
     * @param timeBetweenCalls The allowed time duration between two consecutive calls
     * @param retrySleepDuration The sleep duration before re-attempting a call when {@link #timeBetweenCalls} is violated.
     */
    public RateLimitAvoider(Duration timeBetweenCalls, Duration retrySleepDuration) {
        this.lock = new ReentrantLock(true);
        this.timeBetweenCalls = timeBetweenCalls;
        this.retryPolicy = new RetryPolicy<>().handleResult(null)
                                              .withMaxRetries(-1)
                                              .abortOn(Objects::nonNull)
                                              .withDelay(retrySleepDuration);

        this.lastCallTime = Instant.now().minus(timeBetweenCalls);
    }

    /**
     * Checks if a request can be immediately processed without triggering a rate-limit.
     */
    public boolean canProcess() {
        if (lock.isLocked() && !lock.isHeldByCurrentThread()) {
            return false;
        }
        return lastCallTime.plus(timeBetweenCalls).isBefore(Instant.now());
    }

    /**
     * Calls should go through this method to avoid being rate-limited.
     * @param callable The {@link Callable} that should be used to return a value.
     * @param <T> Type parameter of the {@code callable} parameter.
     * @return Object returned by the {@code callable} parameter.
     * @throws Throwable {@link InterruptedException} if the thread gets interrupted as well as any exceptions thrown inside the {@code callable} parameter.
     */
    public <T> T process(Callable<T> callable) throws Throwable {
        try {
            lock.lockInterruptibly();
            T result = Failsafe.with(retryPolicy).get(() -> canProcess() ? callable.call() : null);
            lastCallTime = Instant.now();
            return result;
        } finally {
            lock.unlock();
        }
    }
}
