package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;

public interface BTCExplorer {

    /**
     * Retrieves an address with all the transactions linked to it
     * @param address Bitcoin address.
     * @return {@link BTCAddress}
     * @throws Throwable Any exception or error thrown during the process.
     */
    BTCAddress getAddress(String address) throws Throwable;

    /**
     * Retrieves a transaction by its hash.
     * @param hash Transaction hash.
     * @return {@link BTCTransaction}
     * @throws Throwable Any exception or error thrown during the process.
     */
    BTCTransaction getTransaction(String hash) throws Throwable;
}
