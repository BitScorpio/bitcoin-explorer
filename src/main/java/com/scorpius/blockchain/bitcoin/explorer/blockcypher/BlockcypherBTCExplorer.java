package com.scorpius.blockchain.bitcoin.explorer.blockcypher;

import com.scorpius.blockchain.RateLimitAvoider;
import com.scorpius.blockchain.bitcoin.explorer.BTCAddress;
import com.scorpius.blockchain.bitcoin.explorer.BTCExplorer;
import com.scorpius.blockchain.bitcoin.explorer.BTCTransaction;
import dev.yasper.rump.Rump;
import java.time.Duration;
import java.util.concurrent.Callable;
import lombok.Getter;
import lombok.Setter;

/**
 * An API implementation for the <a href="https://www.blockcypher.com/dev/bitcoin/">Blockciper API</a>.
 */
// TODO: JavaDocs
public class BlockcypherBTCExplorer implements BTCExplorer {

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

    public BlockcypherBTCExplorer() {
        this.rateLimitAvoider = new RateLimitAvoider(Duration.ofSeconds(18), Duration.ofMillis(1));
    }

    @Override
    // TODO: Obtain all transactions
    public BTCAddress getAddress(String address) throws Exception {
        BTCAddress btcAddress = getAddress(address, -1);
        for (int offset = MAX_TXS_PER_CALL; offset < btcAddress.getTransactionsCount(); offset += MAX_TXS_PER_CALL) {
            int availableTxsCount = btcAddress.getTransactions().size();
            BTCTransaction lastTransaction = btcAddress.getTransactions().get(availableTxsCount - 1);
            btcAddress.getTransactions().addAll(getAddress(address, lastTransaction.getBlockHeight()).getTransactions());
        }
        return btcAddress;
    }

    public BTCAddress getAddress(String address, long beforeBlockHeight) throws Exception {
        String beforeParam = beforeBlockHeight >= 0 ? "&before=" + beforeBlockHeight : "";
        Callable<BTCAddress> callable = () -> Rump.get(SINGLE_ADDRESS + address + "/full?limit=" + MAX_TXS_PER_CALL + "&txlimit=1000000" + beforeParam, BlockcypherBTCAddress.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }

    @Override
    public BTCTransaction getTransaction(String hash) throws Exception {
        Callable<BTCTransaction> callable = () -> Rump.get(SINGLE_TRANSACTION + hash + "?limit=1000000", BlockcypherBTCTransaction.class).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }
}
