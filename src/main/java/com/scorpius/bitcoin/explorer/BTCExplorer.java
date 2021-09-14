package com.scorpius.bitcoin.explorer;

import com.scorpius.bitcoin.RateLimitAvoider;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import lombok.Getter;

/**
 * An API implementation for the <a href="https://www.blockchain.com/api">Blockchain Explorer API</a>.
 */
public abstract class BTCExplorer {

    @Getter
    @Nullable
    protected final RateLimitAvoider rateLimitAvoider;

    /**
     * Creates an instance with a custom {@link RateLimitAvoider}
     * @param rateLimitAvoider Provided {@link RateLimitAvoider}
     */
    protected BTCExplorer(@Nullable RateLimitAvoider rateLimitAvoider) {
        this.rateLimitAvoider = rateLimitAvoider;
    }

    /**
     * Retrieves an address with all the transactions linked to it, ordered from latest to oldest.
     * <br><br>
     * <strong>Note:</strong> This method may take a very long time to return a result if the API provider limits the number of transactions returned in one request; if that is the case, the method {@link #getAddressWithCombinedTransactions(String, BTCAddress)} is utilized for multiple API requests.
     * @param address Bitcoin address.
     * @return The requested {@link BTCAddress} object.
     * @throws Exception {@link java.io.IOException} if the HTTP request fails as well as any exceptions thrown by {@link RateLimitAvoider#process(Callable)}.
     */
    public BTCAddress getAddress(String address) throws Exception {
        BTCAddress btcAddress = null;
        do {
            btcAddress = getAddressWithCombinedTransactions(address, btcAddress);
        } while (btcAddress.getTransactions().size() < btcAddress.getTransactionsCount());
        return btcAddress;
    }

    /**
     * Adds missing transactions to an existing {@link BTCAddress}.
     * @param address Target address.
     * @param existingAddress The latest {@link BTCAddress} obtained to offset transactions from.
     * @return The requested {@link BTCAddress} object.
     * @throws Exception Any exception thrown during the process.
     */
    protected abstract BTCAddress getAddressWithCombinedTransactions(String address, @Nullable BTCAddress existingAddress) throws Exception;

    /**
     * Retrieves a transaction by its hash.
     * @param hash Transaction hash.
     * @return The requested {@link BTCTransaction} object.
     * @throws Exception Any exception thrown during the process.
     */
    public abstract BTCTransaction getTransaction(String hash) throws Exception;
}
