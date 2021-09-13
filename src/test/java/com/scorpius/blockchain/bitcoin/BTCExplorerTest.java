package com.scorpius.blockchain.bitcoin;

import com.scorpius.blockchain.bitcoin.explorers.BTCExplorer;
import com.scorpius.blockchain.bitcoin.explorers.BlockchainBTCExplorer;
import com.scorpius.blockchain.bitcoin.explorers.BlockcipherBTCExplorer;
import com.scorpius.blockchain.bitcoin.pojos.BTCAddress;
import com.scorpius.blockchain.bitcoin.pojos.BTCInput;
import com.scorpius.blockchain.bitcoin.pojos.BTCOutput;
import com.scorpius.blockchain.bitcoin.pojos.BTCTransaction;
import java.util.Arrays;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class BTCExplorerTest {

    private final BTCExplorer[] explorers = {
        new BlockchainBTCExplorer(),
        new BlockcipherBTCExplorer()
    };

    private final String[] addresses = {
        "3EzsqqUTuQRzyAz9uJNKKnoWqTrJisr9j3",
        "1KRdZbn6BqfuALTEQJsmX24h8hG8C3YobM",
        "12RUqdJfKR44q6fJd3RbbfSfST84tvSCAV",
        "3D7VugNn3u8kMZXj1dSYvwHUsy92b85d94"};

    private final String[] txHashes = {
        "659135664894e50040830edb516a76f704fd2be409ecd8d1ea9916c002ab28a2",
        "5143cf232576ae53e8991ca389334563f14ea7a7c507a3e081fbef2538c84f6e",
        "38d97c6bfecdbe201e17787534674bccb57f292a19a99f9f421ebbf6347fbd45",
        "5b361631bd5c47a5476cd3a1f216ab8c6219421c2d3c660fe23041b309e655a7"};

    @Test
    @SneakyThrows
    void getAddress() {
        for (BTCExplorer explorer : explorers) {
            log.debug("Testing explorer: " + explorer.getClass().getSimpleName());
            for (String addr : addresses) {
                log.debug("Testing address: " + addr);
                BTCAddress address = explorer.getAddress(addr);
                log.debug("Loaded {}/{} transactions", address.getTransactions().size(), address.getTransactionsCount());

                long totalSent = address.getTransactions()
                                        .stream()
                                        .filter(tx -> Arrays.stream(tx.getInputs()).anyMatch(in -> in.getAddress().equals(address.getHash())))
                                        .flatMap(tx -> Arrays.stream(tx.getInputs()))
                                        .filter(in -> in.getAddress().equals(address.getHash()))
                                        .mapToLong(BTCInput::getSatoshis)
                                        .sum();

                long totalReceived = address.getTransactions()
                                            .stream()
                                            .filter(tx -> Arrays.stream(tx.getOutputs()).anyMatch(out -> out.getAddress().equals(address.getHash())))
                                            .flatMap(tx -> Arrays.stream(tx.getOutputs()))
                                            .filter(out -> out.getAddress().equals(address.getHash()))
                                            .mapToLong(BTCOutput::getSatoshis)
                                            .sum();

                // Test if sent/received/balance values match together
                Assertions.assertEquals(address.getBalance(), address.getReceived() - address.getSent());

                // Test if sent/received/balance match values from transactions
                Assertions.assertEquals(address.getSent(), totalSent);
                Assertions.assertEquals(address.getReceived(), totalReceived);
                Assertions.assertEquals(address.getBalance(), totalReceived - totalSent);
            }
        }
    }

    @Test
    @SneakyThrows
    void getTransaction() {
        for (BTCExplorer explorer : explorers) {
            log.debug("Testing explorer: " + explorer.getClass().getSimpleName());
            for (String txHash : txHashes) {
                log.debug("Testing transaction: " + txHash);
                BTCTransaction tx = explorer.getTransaction(txHash);

                long txInputValue = Arrays.stream(tx.getInputs())
                                          .mapToLong(BTCInput::getSatoshis)
                                          .sum();

                long txOutputValue = Arrays.stream(tx.getOutputs())
                                           .mapToLong(BTCOutput::getSatoshis)
                                           .sum();

                Assertions.assertEquals(txHash, tx.getHash());
                Assertions.assertEquals(tx.getInputsCount(), tx.getInputs().length);
                Assertions.assertEquals(tx.getOutputsCount(), tx.getOutputs().length);
                Assertions.assertEquals(txInputValue, txOutputValue + tx.getFee());
            }
        }
    }
}