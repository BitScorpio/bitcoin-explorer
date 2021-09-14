package com.scorpius.blockchain.bitcoin.explorer;

/**
 * An API implementation for the <a href="https://www.blockchain.com/api">Blockchain Explorer API</a>.
 */
public interface BTCExplorer {

    /**
     * Retrieves an address with all the transactions linked to it, ordered from latest to oldest.
     * @param address Target address.
     * @return The requested {@link BTCAddress} object.
     * @throws Exception Any exception thrown during the process.
     */
    BTCAddress getAddress(String address) throws Exception;

    /**
     * Retrieves a transaction by its hash.
     * @param hash Transaction hash.
     * @return The requested {@link BTCTransaction} object.
     * @throws Exception Any exception thrown during the process.
     */
    BTCTransaction getTransaction(String hash) throws Exception;
}
