package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.bitcoin.record.BTCAddress;
import javax.annotation.Nullable;

public interface MultiRequestBTCExplorer extends BTCExplorer {

    @Override
    default BTCAddress getAddress(String address) throws Exception {
        BTCAddress btcAddress = null;
        do {
            btcAddress = getAddressCombineTransactions(address, btcAddress);
        } while (btcAddress.transactions().size() < btcAddress.transactionsCount());
        return btcAddress;
    }

    BTCAddress getAddressCombineTransactions(String address, @Nullable BTCAddress existingAddress) throws Exception;
}
