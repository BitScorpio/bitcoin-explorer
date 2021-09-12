package com.scorpius.blockchain.bitcoin;

import dev.yasper.rump.Rump;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.Getter;
import lombok.Setter;

public class BTCExplorer {

    private static final String BASE = "https://blockchain.info/";
    private static final String SINGLE_TRANSACTION = BASE + "rawtx/";
    private static final String SINGLE_ADDRESS = BASE + "rawaddr/";

    private final Lock lock;

    @Getter
    @Setter
    private Duration timePerRequest;
    private Instant lastRequestInstant;

    public BTCExplorer() {
        this.lock = new ReentrantLock(true);
        this.timePerRequest = Duration.ofSeconds(10);
        this.lastRequestInstant = Instant.now().minus(timePerRequest);
    }

    // TODO: Get all transactions, currently limited to 50 by blockchain.info
    public BTCAddress getAddress(String address) throws Exception {
        return process(() -> Rump.get(SINGLE_ADDRESS + address, BTCAddress.class).getBody());
    }

    public BTCTransaction getTransaction(String hash) throws Exception {
        return process(() -> Rump.get(SINGLE_TRANSACTION + hash, BTCTransaction.class).getBody());
    }

    private <T> T process(Callable<T> callable) throws Exception {
        if (lastRequestInstant.plus(timePerRequest).isBefore(Instant.now())) {
            try {
                lock.lockInterruptibly();
                return callable.call();
            } finally {
                lastRequestInstant = Instant.now();
                lock.unlock();
            }
        } else {
            Thread.sleep(1);
            return process(callable);
        }
    }
}
