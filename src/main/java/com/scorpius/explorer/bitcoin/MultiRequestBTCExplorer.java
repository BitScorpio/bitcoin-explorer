package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.bitcoin.record.BTCAddress;
import javax.annotation.Nullable;

public abstract class MultiRequestBTCExplorer implements BTCExplorer {

    @Override
    public final BTCAddress getAddress(String address) throws Exception {
        BTCAddress btcAddress = null;
        do {
            btcAddress = getAddressCombineTransactions(address, btcAddress);
        } while (btcAddress.transactions().size() < btcAddress.transactionsCount());
        return btcAddress;
    }

    protected abstract BTCAddress getAddressCombineTransactions(String address, @Nullable BTCAddress existingAddress) throws Exception;
}
