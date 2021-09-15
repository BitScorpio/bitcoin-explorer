package com.scorpius.explorer.bitcoin.deserializer;

import com.scorpius.explorer.SimpleJsonDeserializer;
import com.scorpius.explorer.bitcoin.record.BTCAddress;

public abstract class BTCAddressDeserializer extends SimpleJsonDeserializer<BTCAddress> {

    protected final BTCTransactionDeserializer transactionDeserializer;

    protected BTCAddressDeserializer(BTCTransactionDeserializer transactionDeserializer) {
        this.transactionDeserializer = transactionDeserializer;
    }
}
