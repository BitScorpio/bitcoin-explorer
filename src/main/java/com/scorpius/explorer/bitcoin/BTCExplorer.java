package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;

public interface BTCExplorer {

    /**
     * Retrieves an address with all the transactions linked to it
     * @param address Bitcoin address.
     * @return {@link BTCAddress}
     * @throws Exception Any exception thrown during the process.
     */
    BTCAddress getAddress(String address) throws Exception;

    /**
     * Retrieves a transaction by its hash.
     * @param hash Transaction hash.
     * @return {@link BTCTransaction}
     * @throws Exception Any exception thrown during the process.
     */
    BTCTransaction getTransaction(String hash) throws Exception;
}
