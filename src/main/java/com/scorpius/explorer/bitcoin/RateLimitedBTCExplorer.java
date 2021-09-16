package com.scorpius.explorer.bitcoin;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.scorpius.explorer.RateLimitAvoider;
import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCRecordResponseTransformer;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;
import dev.yasper.rump.config.RequestConfig;
import dev.yasper.rump.response.ResponseTransformer;
import java.time.Duration;

/**
 * Obtains {@link BTCAddress} over multiple requests while honoring enforced rate-limits.
 */
public abstract class RateLimitedBTCExplorer extends MultiRequestBTCExplorer {

    /**
     * Used by <a href="https://github.com/Jasper-ketelaar/Rump">Rump</a> to deserialize JSON objects.
     */
    protected final RequestConfig requestConfig;

    protected final RateLimitAvoider rateLimitAvoider;

    protected RateLimitedBTCExplorer(Duration timeBetweenCalls) {
        this(new RateLimitAvoider(timeBetweenCalls, Duration.ofMillis(200)));
    }

    protected RateLimitedBTCExplorer(RateLimitAvoider rateLimitAvoider) {
        ResponseTransformer responseTransformer = new BTCRecordResponseTransformer(createAddressDeserializer(), createTransactionDeserializer());
        this.requestConfig = new RequestConfig().setResponseTransformer(responseTransformer);
        this.rateLimitAvoider = rateLimitAvoider;
    }

    public final RateLimitAvoider getRateLimitAvoider() {
        return rateLimitAvoider;
    }

    protected abstract JsonDeserializer<BTCAddress> createAddressDeserializer();

    protected abstract JsonDeserializer<BTCTransaction> createTransactionDeserializer();
}
