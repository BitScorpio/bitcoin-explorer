package io.github.bitscorpio.bitcoin;

import com.fasterxml.jackson.databind.JsonDeserializer;
import dev.yasper.rump.config.RequestConfig;
import dev.yasper.rump.response.ResponseTransformer;
import io.github.bitscorpio.RateLimitAvoider;
import io.github.bitscorpio.bitcoin.record.BTCAddress;
import io.github.bitscorpio.bitcoin.record.BTCRecordResponseTransformer;
import io.github.bitscorpio.bitcoin.record.BTCTransaction;
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

    /**
     * Creates a {@link BTCAddress} JSON deserializer.
     * @return {@link JsonDeserializer}
     */
    protected abstract JsonDeserializer<BTCAddress> createAddressDeserializer();

    /**
     * Creates a {@link BTCTransaction} JSON deserializer.
     * @return {@link JsonDeserializer}
     */
    protected abstract JsonDeserializer<BTCTransaction> createTransactionDeserializer();
}
