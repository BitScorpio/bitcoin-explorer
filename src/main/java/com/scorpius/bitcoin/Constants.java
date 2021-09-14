package com.scorpius.bitcoin;

import java.time.Duration;

/**
 * Constant values used across the library
 */
public class Constants {

    /**
     * The default sleep duration for any retry logic.
     */
    public static final Duration DEFAULT_RETRY_SLEEP_DURATION = Duration.ofMillis(200);

    /**
     * Calculated based on rate-limits enforced by the <a href="https://www.blockchain.com/api">Blockchain Explorer API</a>.
     */
    public static final Duration BLOCKCHAIN_DURATION_PER_CALL = Duration.ofSeconds(5);

    /**
     * Calculated based on rate-limits enforced by the <a href="https://www.blockcypher.com/dev/bitcoin/">Blockciper API</a>.
     */
    public static final Duration BLOCKCYPHER_DURATION_PER_CALL = Duration.ofSeconds(18);
}
