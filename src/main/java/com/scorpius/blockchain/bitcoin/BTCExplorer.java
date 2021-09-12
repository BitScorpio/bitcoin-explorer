package com.scorpius.blockchain.bitcoin;

import dev.yasper.rump.Rump;
import java.io.IOException;

// TODO: Respect blockchain.info's ratelimits, 1 request every 10 seconds
public class BTCExplorer {

    private static final String BASE = "https://blockchain.info/";
    private static final String SINGLE_TRANSACTION = BASE + "rawtx/";
    private static final String SINGLE_ADDRESS = BASE + "rawaddr/";

    // TODO: Get all transactions, currently limited to 50 by blockchain.info
    public BTCAddress getAddress(String address) throws IOException {
        return Rump.get(SINGLE_ADDRESS + address, BTCAddress.class).getBody();
    }

    public BTCTransaction getTransaction(String hash) throws IOException {
        return Rump.get(SINGLE_TRANSACTION + hash, BTCTransaction.class).getBody();
    }
}
