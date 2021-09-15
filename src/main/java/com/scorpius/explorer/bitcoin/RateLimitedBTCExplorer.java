package com.scorpius.explorer.bitcoin;

import com.scorpius.explorer.RateLimitAvoider;
import com.scorpius.explorer.bitcoin.deserializer.BTCAddressDeserializer;
import com.scorpius.explorer.bitcoin.deserializer.BTCTransactionDeserializer;
import java.time.Duration;

public abstract class RateLimitedBTCExplorer extends WebDataSourceBTCExplorer implements MultiRequestBTCExplorer {

    protected final RateLimitAvoider rateLimitAvoider;

    public RateLimitedBTCExplorer(Duration durationPerCall, BTCAddressDeserializer addressDeserializer, BTCTransactionDeserializer transactionDeserializer) {
        super(addressDeserializer, transactionDeserializer);
        this.rateLimitAvoider = new RateLimitAvoider(durationPerCall, Duration.ofMillis(200));
    }

    public final RateLimitAvoider getRateLimitAvoider() {
        return rateLimitAvoider;
    }
}
