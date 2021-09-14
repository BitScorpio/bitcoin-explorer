package com.scorpius.bitcoin.explorer.blockchain;

import com.scorpius.bitcoin.Constants;
import com.scorpius.bitcoin.RateLimitAvoider;
import com.scorpius.bitcoin.explorer.BTCAddress;
import com.scorpius.bitcoin.explorer.BTCExplorer;
import com.scorpius.bitcoin.explorer.BTCTransaction;
import dev.yasper.rump.Rump;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;

/**
 * An API implementation for the <a href="https://www.blockchain.com/api">Blockchain Explorer API</a>.
 */
public class BlockchainBTCExplorer extends BTCExplorer {

    private static final String API_BASE = "https://blockchain.info/";
    private static final String API_ADDRESS = API_BASE + "rawaddr/";
    private static final String API_TRANSACTION = API_BASE + "rawtx/";

    /**
     * Maximum amount of transactions retrieved per API request as dictated by the <a href="https://www.blockchain.com/api/blockchain_api">Blockchain Data API</a>
     */
    public static final int MAX_TXS_PER_CALL = 50;

    /**
     * Creates an instance with {@link Constants#BLOCKCHAIN_DURATION_PER_CALL} & {@link Constants#DEFAULT_RETRY_SLEEP_DURATION}, see {@link RateLimitAvoider} for more details.
     */
    public BlockchainBTCExplorer() {
        this(new RateLimitAvoider(Constants.BLOCKCHAIN_DURATION_PER_CALL, Constants.DEFAULT_RETRY_SLEEP_DURATION));
    }

    /**
     * Creates an instance with a custom {@link RateLimitAvoider}
     * @param rateLimitAvoider Provided {@link RateLimitAvoider}
     */
    public BlockchainBTCExplorer(@Nullable RateLimitAvoider rateLimitAvoider) {
        super(rateLimitAvoider);
    }

    /**
     * Refer to documentation at {@link BTCExplorer#getAddress(String)} and {@link BTCExplorer#getAddressWithCombinedTransactions(String, BTCAddress)}
     */
    @Override
    protected BTCAddress getAddressWithCombinedTransactions(String address, BTCAddress existingAddress) throws Exception {
        int offset = existingAddress == null ? 0 : existingAddress.getTransactions().size();
        Callable<BTCAddress> callable = () -> Rump.get(API_ADDRESS + address + "?limit=" + MAX_TXS_PER_CALL + "&offset=" + offset, BlockchainBTCAddress.class).getBody();
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
        Callable<BTCTransaction> callable = () -> Rump.get(API_TRANSACTION + hash, BlockchainBTCTransaction.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }
}
