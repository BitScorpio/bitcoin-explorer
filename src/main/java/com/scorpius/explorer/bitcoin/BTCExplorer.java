package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;

public interface BTCExplorer {

    BTCAddress getAddress(String address) throws Exception;

    BTCTransaction getTransaction(String hash) throws Exception;
}
