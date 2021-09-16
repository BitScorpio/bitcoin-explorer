package io.github.bitscorpio.bitcoin.record;

import java.util.List;

public record BTCAddress(String hash, long balance, long transactionsCount, List<BTCTransaction> transactions) {

    public void combineTransactions(BTCAddress address) {
        if (!hash().equals(address.hash())) {
            throw new AddressDoesNotMatchException(hash, address.hash());
        }
        transactions().addAll(address.transactions());
    }

    public static final class AddressDoesNotMatchException extends RuntimeException {

        private AddressDoesNotMatchException(String expected, String actual) {
            super("Expected: " + expected + " but found " + actual);
        }
    }
}
