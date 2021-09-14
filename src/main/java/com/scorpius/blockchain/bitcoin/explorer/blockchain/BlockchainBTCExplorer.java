package com.scorpius.blockchain.bitcoin.explorer.blockchain;

import com.scorpius.blockchain.RateLimitAvoider;
import com.scorpius.blockchain.bitcoin.explorer.BTCAddress;
import com.scorpius.blockchain.bitcoin.explorer.BTCExplorer;
import com.scorpius.blockchain.bitcoin.explorer.BTCTransaction;
import dev.yasper.rump.Rump;
import java.time.Duration;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.Setter;

/**
 * An API implementation for the <a href="https://www.blockchain.com/api">Blockchain Explorer API</a>.
 */
public class BlockchainBTCExplorer implements BTCExplorer {

    private static final String BASE = "https://blockchain.info/";
    private static final String SINGLE_ADDRESS = BASE + "rawaddr/";
    private static final String SINGLE_TRANSACTION = BASE + "rawtx/";

    /**
     * Maximum amount of transactions retrieved per API request as dictated by the <a href="https://www.blockchain.com/api/blockchain_api">Blockchain Data API</a>
     */
    public static final int MAX_TXS_PER_CALL = 50;

    @Getter
    @Setter
    private RateLimitAvoider rateLimitAvoider;

    /**
     * Creates an instance with 5 seconds duration per call & 1 millisecond timeout, see {@link RateLimitAvoider} for more details.
     */
    public BlockchainBTCExplorer() {
        this.rateLimitAvoider = new RateLimitAvoider(Duration.ofSeconds(5), Duration.ofMillis(1));
    }

    /**
     * Retrieves an address with all the transactions linked to it, ordered from latest to oldest.
     * <pre><strong>Note:</strong> This method might take a very long time to return a result depending on how many transactions are associated with the provided address since it performs multiple API requests when there are more than {@link #MAX_TXS_PER_CALL} transactions. For an alternative see {@link #getAddress(String, int)}.</pre>
     * @param address Bitcoin address.
     * @return The requested {@link BTCAddress} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    @Override
    public BTCAddress getAddress(String address) throws Exception {
        BTCAddress btcAddress = getAddress(address, 0);
        for (int offset = MAX_TXS_PER_CALL; offset < btcAddress.getTransactionsCount(); offset += MAX_TXS_PER_CALL) {
            btcAddress.getTransactions().addAll(getAddress(address, offset).getTransactions());
        }
        return btcAddress;
    }

    /**
     * Retrieves an address with the <strong>latest {@link #MAX_TXS_PER_CALL} transactions</strong> linked to it <strong>starting from the desired offset</strong>.
     * <pre><strong>Note:</strong> Transactions are ordered order from latest to oldest, using <strong>0 (zero)</strong> as an offset obtains the most recent {@link #MAX_TXS_PER_CALL} transactions.</pre>
     * @param address Base58 or hash160 address.
     * @param transactionsOffset The offset to begin obtaining transactions from
     * @return The requested {@link BTCAddress} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    public BTCAddress getAddress(String address, int transactionsOffset) throws Exception {
        Callable<BTCAddress> callable = () -> Rump.get(SINGLE_ADDRESS + address + "?limit=" + MAX_TXS_PER_CALL + "&offset=" + transactionsOffset, BlockchainBTCAddress.class).getBody();
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
    @Override
    public BTCTransaction getTransaction(String hash) throws Exception {
        Callable<BTCTransaction> callable = () -> Rump.get(SINGLE_TRANSACTION + hash, BlockchainBTCTransaction.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }
}
