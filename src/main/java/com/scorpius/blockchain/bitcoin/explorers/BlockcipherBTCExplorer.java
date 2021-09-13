package com.scorpius.blockchain.bitcoin.explorers;

import com.scorpius.blockchain.RateLimitAvoider;
import com.scorpius.blockchain.bitcoin.pojos.BTCAddress;
import com.scorpius.blockchain.bitcoin.pojos.BTCTransaction;
import com.scorpius.blockchain.bitcoin.pojos.BlockcipherBTCAddress;
import com.scorpius.blockchain.bitcoin.pojos.BlockcipherBTCTransaction;
import dev.yasper.rump.Rump;
import java.time.Duration;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.Setter;

/**
 * An API implementation for the <a href="https://www.blockcypher.com/dev/bitcoin/">Blockciper API</a>.
 */
// TODO: JavaDocs
public class BlockcipherBTCExplorer implements BTCExplorer {

    private static final String BASE = "https://api.blockcypher.com/v1/btc/main/";
    private static final String SINGLE_ADDRESS = BASE + "addrs/";
    private static final String SINGLE_TRANSACTION = BASE + "txs/";

    /**
     * Maximum amount of transactions retrieved per API request as dictated by the <a href="https://www.blockcypher.com/dev/bitcoin/#address-full-endpoint">Blockciper Address API</a>
     */
    public static final int MAX_TXS_PER_CALL = 50;

    @Getter
    @Setter
    private RateLimitAvoider rateLimitAvoider;

    public BlockcipherBTCExplorer() {
        this.rateLimitAvoider = new RateLimitAvoider(Duration.ofSeconds(18), Duration.ofMillis(1));
    }

    @Override
    // TODO: Obtain all transactions
    public BTCAddress getAddress(String address) throws Exception {
        Callable<BTCAddress> callable = () -> Rump.get(SINGLE_ADDRESS + address + "/full?limit=" + MAX_TXS_PER_CALL, BlockcipherBTCAddress.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }

    @Override
    public BTCTransaction getTransaction(String hash) throws Exception {
        Callable<BTCTransaction> callable = () -> Rump.get(SINGLE_TRANSACTION + hash, BlockcipherBTCTransaction.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }
}
