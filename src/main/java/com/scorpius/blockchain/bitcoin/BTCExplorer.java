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

    /**
     * Maximum amount of transactions retrieved per API request as dictated by the <a href="https://www.blockchain.com/api/blockchain_api">Blockchain Data API</a>
     */
    public static final int MAX_TXS_PER_CALL = 50;

    @Getter
    @Setter
    private RateLimitAvoider rateLimitAvoider;

    public BTCExplorer() {
        this.rateLimitAvoider = new RateLimitAvoider(Duration.ofSeconds(5));
    }

    /**
     * Retrieves an address with the <strong>latest 50 transactions</strong> linked to it, see {@link BTCAddress} for specifics.
     * @param address Address or Hash160.
     * @return The requested {@link BTCAddress} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    public BTCAddress getAddress(String address) throws Exception {
        return getAddress(address, 0);
    }

    /**
     * Retrieves an address with the <strong>latest 50 transactions</strong> linked to it <strong>starting from the desired offset</strong>,
     * see {@link BTCAddress} for specifics.
     * <pre><strong>Note:</strong> Transactions are ordered order from latest to oldest, using <strong>1</strong> as an offset ignores the most recent transaction.</pre>
     * @param address Address or Hash160.
     * @param transactionsOffset The offset to begin obtaining transactions from
     * @return The requested {@link BTCAddress} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    public BTCAddress getAddress(String address, int transactionsOffset) throws Exception {
        Callable<BTCAddress> callable = () -> Rump.get(SINGLE_ADDRESS + address + "?limit=" + MAX_TXS_PER_CALL + "&offset=" + transactionsOffset, BTCAddress.class).getBody();
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
