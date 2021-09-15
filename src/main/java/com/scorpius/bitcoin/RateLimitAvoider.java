package com.scorpius.bitcoin;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;

/**
 * Regulates code execution to avoid rate-limits
 */
@Slf4j
public class RateLimitAvoider {

    /**
     * Used to ensure that only one threads is using the avoider.
     */
    private final ReentrantLock lock;

    /**
     * The duration between two consecutive calls.
     */
    private final Duration durationPerCall;

    /**
     * The sleep duration before re-attempting a call when {@link #durationPerCall} is violated.
     */
    private final Duration retrySleepDuration;

    private Instant lastCallTime;

    /**
     * Creates an instance that regulates code execution according to the given parameters.
     * @param durationPerCall The allowed duration between two consecutive calls
     * @param retrySleepDuration The sleep duration before re-attempting a call when {@link #durationPerCall} is violated.
     */
    public RateLimitAvoider(Duration durationPerCall, Duration retrySleepDuration) {
        this.lock = new ReentrantLock(true);
        this.durationPerCall = durationPerCall;
        this.retrySleepDuration = retrySleepDuration;
        this.lastCallTime = Instant.now().minus(durationPerCall);
    }

    /**
     * Checks if a request can be immediately processed without triggering a rate-limit.
     */
    public boolean canProcess() {
        if (lock.isLocked() && !lock.isHeldByCurrentThread()) {
            return false;
        }
        return lastCallTime.plus(durationPerCall).isBefore(Instant.now());
    }

    /**
     * Calls should go through this method to avoid being rate-limited.
     * @param callable The {@link Callable} that should be used to return a value.
     * @param <T> Type parameter of the {@code callable} parameter.
     * @return Object returned by the {@code callable} parameter.
     * @throws Exception {@link InterruptedException} if the thread gets interrupted as well as any exceptions thrown inside the {@code callable} parameter.
     */
    public <T> T process(Callable<T> callable) throws Exception {
        try {
            lock.lockInterruptibly();
            if (canProcess()) {
                T result = callable.call();
                lastCallTime = Instant.now();
                return result;
            } else {
                Thread.sleep(retrySleepDuration.toMillis());
                return process(callable);
            }
        } finally {
            lock.unlock();
        }
    }
}
