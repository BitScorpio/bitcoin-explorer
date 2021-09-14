package com.scorpius.bitcoin.explorer.blockcypher;

import com.scorpius.bitcoin.Constants;
import com.scorpius.bitcoin.RateLimitAvoider;
import com.scorpius.bitcoin.explorer.BTCAddress;
import com.scorpius.bitcoin.explorer.BTCExplorer;
import com.scorpius.bitcoin.explorer.BTCTransaction;
import dev.yasper.rump.Rump;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

/**
 * An API implementation for the <a href="https://www.blockcypher.com/dev/bitcoin/">Blockciper API</a>.
 */
public class BlockcypherBTCExplorer extends BTCExplorer {

    private static final String API_BASE = "https://api.blockcypher.com/v1/btc/main/";
    private static final String API_ADDRESS = API_BASE + "addrs/";
    private static final String API_TRANSACTION = API_BASE + "txs/";

    /**
     * Maximum amount of transactions retrieved per API request as dictated by the <a href="https://www.blockcypher.com/dev/bitcoin/#address-full-endpoint">Blockciper Address API</a>
     */
    public static final int MAX_TXS_PER_CALL = 50;

    /**
     * Creates an instance with 18 seconds duration per call & {@link Constants#DEFAULT_RETRY_SLEEP_DURATION}, see {@link RateLimitAvoider} for more details.
     */
    public BlockcypherBTCExplorer() {
        this(new RateLimitAvoider(Duration.ofSeconds(18), Constants.DEFAULT_RETRY_SLEEP_DURATION));
    }

    /**
     * Creates an instance with a custom {@link RateLimitAvoider}
     * @param rateLimitAvoider Provided {@link RateLimitAvoider}
     */
    public BlockcypherBTCExplorer(@Nullable RateLimitAvoider rateLimitAvoider) {
        super(rateLimitAvoider);
    }

    /**
     * Refer to documentation at {@link BTCExplorer#getAddress(String)} and {@link BTCExplorer#getAddressWithCombinedTransactions(String, BTCAddress)}
     */
    @Override
    protected BTCAddress getAddressWithCombinedTransactions(String address, BTCAddress existingAddress) throws Exception {
        StringBuilder beforeParamBuilder = new StringBuilder();
        if (existingAddress != null && existingAddress.getTransactions().size() > 0) {
            List<BTCTransaction> transactions = existingAddress.getTransactions();
            beforeParamBuilder.append("&before=").append(transactions.get(transactions.size() - 1).getBlockHeight());
        }
        Callable<BTCAddress> callable = () -> Rump.get(API_ADDRESS + address + "/full?limit=" + MAX_TXS_PER_CALL + "&txlimit=1000000" + beforeParamBuilder, BlockcypherBTCAddress.class).getBody();
        BTCAddress newAddress = rateLimitAvoider == null ? callable.call() : rateLimitAvoider.process(callable);
        if (existingAddress == null) {
            return newAddress;
        }
        existingAddress.combineTransactions(newAddress);
        return existingAddress;
    }

    /**
     * Retrieves a transaction by its hash.
     * @param hash Transaction hash.
     * @return The requested {@link BTCTransaction} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    @Override
    public BTCTransaction getTransaction(String hash) throws Exception {
        Callable<BTCTransaction> callable = () -> Rump.get(API_TRANSACTION + hash + "?limit=1000000", BlockcypherBTCTransaction.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }
}
