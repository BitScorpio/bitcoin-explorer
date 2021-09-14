package com.scorpius.bitcoin.explorer.blockcypher;

import com.scorpius.bitcoin.explorer.BTCAddress;
import com.scorpius.bitcoin.explorer.BTCExplorer;
import com.scorpius.bitcoin.explorer.BTCTransaction;
import com.scorpius.bitcoin.explorer.RateLimitAvoider;
import dev.yasper.rump.Rump;
import java.time.Duration;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.Setter;

/**
 * An API implementation for the <a href="https://www.blockcypher.com/dev/bitcoin/">Blockciper API</a>.
 */
public class BlockcypherBTCExplorer implements BTCExplorer {

    private static final String BASE = "https://api.blockcypher.com/v1/btc/main/";
    private static final String SINGLE_ADDRESS = BASE + "addrs/";
    private static final String SINGLE_TRANSACTION = BASE + "txs/";

    /**
     * Maximum amount of transactions retrieved per API request as dictated by the <a href="https://www.blockcypher.com/dev/bitcoin/#address-full-endpoint">Blockciper Address API</a>
     */
    public static final int MAX_TXS_PER_CALL = 50;

    @Getter
    @Setter
    private RateLimitAvoider rateLimitAvoider;

    /**
     * Creates an instance with 18 seconds duration per call & 1 millisecond timeout, see {@link RateLimitAvoider} for more details.
     */
    public BlockcypherBTCExplorer() {
        this(new RateLimitAvoider(Duration.ofSeconds(18), Duration.ofMillis(1)));
    }

    /**
     * Creates an instance with a custom {@link RateLimitAvoider}
     * @param rateLimitAvoider Provided {@link RateLimitAvoider}
     */
    public BlockcypherBTCExplorer(RateLimitAvoider rateLimitAvoider) {
        this.rateLimitAvoider = rateLimitAvoider;
    }

    /**
     * Retrieves an address with all the transactions linked to it, ordered from latest to oldest.
     * <pre><strong>Note:</strong> This method might take a very long time to return a result depending on how many transactions are associated with the provided address since it performs multiple API requests when there are more than {@link #MAX_TXS_PER_CALL} transactions. For an alternative see {@link #getAddress(String, long)}.</pre>
     * @param address Bitcoin address.
     * @return The requested {@link BTCAddress} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    @Override
    public BTCAddress getAddress(String address) throws Exception {
        BTCAddress btcAddress = getAddress(address, -1);
        for (int offset = MAX_TXS_PER_CALL; offset < btcAddress.getTransactionsCount(); offset += MAX_TXS_PER_CALL) {
            int availableTxsCount = btcAddress.getTransactions().size();
            BTCTransaction lastTransaction = btcAddress.getTransactions().get(availableTxsCount - 1);
            btcAddress.getTransactions().addAll(getAddress(address, lastTransaction.getBlockHeight()).getTransactions());
        }
        return btcAddress;
    }

    /**
     * Retrieves an address with the <strong>latest {@link #MAX_TXS_PER_CALL} transactions</strong> linked to it <strong>before the desired block height</strong>.
     * @param address Base58 or hash160 address.
     * @param beforeBlockHeight The block height to begin obtaining transactions before
     * @return The requested {@link BTCAddress} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    public BTCAddress getAddress(String address, long beforeBlockHeight) throws Exception {
        String beforeParam = beforeBlockHeight >= 0 ? "&before=" + beforeBlockHeight : "";
        Callable<BTCAddress> callable = () -> Rump.get(SINGLE_ADDRESS + address + "/full?limit=" + MAX_TXS_PER_CALL + "&txlimit=1000000" + beforeParam, BlockcypherBTCAddress.class).getBody();
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
        Callable<BTCTransaction> callable = () -> Rump.get(SINGLE_TRANSACTION + hash + "?limit=1000000", BlockcypherBTCTransaction.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }
}
