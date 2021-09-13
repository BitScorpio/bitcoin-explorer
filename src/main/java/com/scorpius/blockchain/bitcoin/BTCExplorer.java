package com.scorpius.blockchain.bitcoin;

import com.scorpius.blockchain.RateLimitAvoider;
import dev.yasper.rump.Rump;
import java.time.Duration;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.Setter;

/**
 * An API implementation for the <a href="https://www.blockchain.com/api">Blockchain Explorer API</a>.
 */
public class BTCExplorer {

    private static final String BASE = "https://blockchain.info/";
    private static final String SINGLE_TRANSACTION = BASE + "rawtx/";
    private static final String SINGLE_ADDRESS = BASE + "rawaddr/";

    @Getter
    @Setter
    private RateLimitAvoider rateLimitAvoider;

    public BTCExplorer() {
        this.rateLimitAvoider = new RateLimitAvoider(Duration.ofSeconds(5));
    }

    /**
     * Retrieves an address and some corresponding data, see {@link BTCAddress} for specifics.
     * @param address Address or Hash160.
     * @return The requested {@link BTCAddress} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    // TODO: Get all transactions, currently limited to 50 by blockchain.info
    public BTCAddress getAddress(String address) throws Exception {
        Callable<BTCAddress> callable = () -> Rump.get(SINGLE_ADDRESS + address, BTCAddress.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }

    /**
     * Retrieves a transaction by its hash.
     * @param hash Transaction hash.
     * @return The requested {@link BTCTransaction} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    public BTCTransaction getTransaction(String hash) throws Exception {
        Callable<BTCTransaction> callable = () -> Rump.get(SINGLE_TRANSACTION + hash, BTCTransaction.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }
}
