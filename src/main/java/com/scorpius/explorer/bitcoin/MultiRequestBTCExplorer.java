package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.bitcoin.record.BTCAddress;

/**
 * Obtains {@link BTCAddress} over multiple requests.
 */
public abstract class MultiRequestBTCExplorer implements BTCExplorer {

    /**
     * Refer to {@link BTCExplorer#getAddress(String)}
     */
    @Override
    public final BTCAddress getAddress(String address) throws Throwable {
        BTCAddress btcAddress = getAddressLatestTransactions(address);
        while (btcAddress.transactions().size() < btcAddress.transactionsCount()) {
            btcAddress = getAddressNextTransactionsBatch(btcAddress);
        }
        return btcAddress;
    }

    /**
     * Retrieves a {@link BTCAddress} with the latest transactions which usually does not include the full transactions list.
     * @param address Bitcoin address.
     * @return {@link BTCAddress}
     * @throws Throwable Any exception or error thrown during the process.
     */
    protected abstract BTCAddress getAddressLatestTransactions(String address) throws Throwable;

    /**
     * Adds the next batch of missing transactions to an existing {@link BTCAddress}.
     * @param existingAddress A previously obtained {@link BTCAddress} to use for deciding which transactions batch should be requested.
     * @return {@link BTCAddress} with more transactions added to it.
     * @throws Throwable Any exception or error thrown during the process.
     */
    protected abstract BTCAddress getAddressNextTransactionsBatch(BTCAddress existingAddress) throws Throwable;
}
