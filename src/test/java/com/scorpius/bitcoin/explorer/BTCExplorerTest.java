package com.scorpius.bitcoin.explorer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.scorpius.bitcoin.explorer.blockchain.BlockchainBTCExplorer;
import com.scorpius.bitcoin.explorer.blockcypher.BlockcypherBTCExplorer;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class BTCExplorerTest {

    private final List<BTCExplorer> explorers;

    private final String[] rawAddresses = {
        "bc1q0hhxpv6u9u0eh3qv5gf4zx2zlvpqn4m7cacxmm",
        "1KRdZbn6BqfuALTEQJsmX24h8hG8C3YobM",
        "12RUqdJfKR44q6fJd3RbbfSfST84tvSCAV",
        "3D7VugNn3u8kMZXj1dSYvwHUsy92b85d94"};

    private final String[] rawTransactions = {
        "659135664894e50040830edb516a76f704fd2be409ecd8d1ea9916c002ab28a2",
        "5143cf232576ae53e8991ca389334563f14ea7a7c507a3e081fbef2538c84f6e",
        "38d97c6bfecdbe201e17787534674bccb57f292a19a99f9f421ebbf6347fbd45",
        "5b361631bd5c47a5476cd3a1f216ab8c6219421c2d3c660fe23041b309e655a7"};

    public BTCExplorerTest() {
        BlockchainBTCExplorer blockchainBTCExplorer = new BlockchainBTCExplorer();
        BlockcypherBTCExplorer blockcypherBTCExplorer = new BlockcypherBTCExplorer();
        MultiBTCExplorer multiBTCExplorer = new MultiBTCExplorer(blockchainBTCExplorer, blockcypherBTCExplorer);
        explorers = List.of(blockchainBTCExplorer, blockcypherBTCExplorer, multiBTCExplorer);
    }

    @Test
    @SneakyThrows
    void getAddress() {
        // Load addresses using all explorers
        Multimap<BTCExplorer, BTCAddress> addresses = ArrayListMultimap.create();
        explorers.parallelStream().forEach(explorer -> {
            for (String rawAddress : rawAddresses) {
                try {
                    log.debug("[{}] [Address]-> {}", explorer.getClass().getSimpleName(), rawAddress);
                    BTCAddress address = explorer.getAddress(rawAddress);
                    Assertions.assertEquals(rawAddress, address.getHash());
                    Assertions.assertNotEquals(0, address.getTransactionsCount());
                    Assertions.assertNotEquals(0, address.getTransactions().size());
                    addresses.put(explorer, address);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        });

        // Check for data consistency across all explorers
        for (BTCExplorer explorer : addresses.keySet()) {
            for (BTCExplorer otherExplorer : addresses.keySet()) {
                if (!explorer.equals(otherExplorer)) {
                    Assertions.assertEquals(addresses.get(explorer), addresses.get(otherExplorer));
                }
            }
        }

        // Check every single address
        for (BTCExplorer explorer : addresses.keySet()) {
            for (BTCAddress address : addresses.get(explorer)) {
                log.debug("[{}] [{}] Loaded {}/{} transactions",
                          address.getClass().getSimpleName(),
                          address.getHash(),
                          address.getTransactions().size(),
                          address.getTransactionsCount());

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

                // Test if balance matches sent/received transactions
                Assertions.assertEquals(address.getBalance(), totalReceived - totalSent);
            }
        }
    }

    @Test
    @SneakyThrows
    void getTransaction() {
        // Load transactions using all explorers
        Multimap<BTCExplorer, BTCTransaction> transactions = ArrayListMultimap.create();
        explorers.parallelStream().forEach(explorer -> {
            for (String rawTransaction : rawTransactions) {
                try {
                    log.debug("[{}] [TX]-> {}", explorer.getClass().getSimpleName(), rawTransaction);
                    BTCTransaction transaction = explorer.getTransaction(rawTransaction);
                    Assertions.assertEquals(rawTransaction, transaction.getHash());
                    Assertions.assertNotEquals(0, transaction.getFee());
                    Assertions.assertNotEquals(0, transaction.getBlockHeight());
                    Assertions.assertNotEquals(0, transaction.getInputsCount());
                    Assertions.assertNotEquals(0, transaction.getOutputsCount());
                    Assertions.assertNotEquals(0, transaction.getInputs().length);
                    Assertions.assertNotEquals(0, transaction.getOutputs().length);
                    transactions.put(explorer, transaction);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(0);
                }
            }
        });

        // Check for data consistency across all explorers
        for (BTCExplorer explorer : transactions.keySet()) {
            for (BTCExplorer otherExplorer : transactions.keySet()) {
                if (!explorer.equals(otherExplorer)) {
                    Assertions.assertEquals(transactions.get(explorer), transactions.get(otherExplorer));
                }
            }
        }

        // Check every single address
        for (BTCExplorer explorer : transactions.keySet()) {
            for (BTCTransaction transaction : transactions.get(explorer)) {
                log.debug("[{}] [{}] Checking", transaction.getClass().getSimpleName(), transaction.getHash());

                long totalInput = Arrays.stream(transaction.getInputs())
                                        .mapToLong(BTCInput::getSatoshis)
                                        .sum();

                long totalOutput = Arrays.stream(transaction.getOutputs())
                                         .mapToLong(BTCOutput::getSatoshis)
                                         .sum();

                Assertions.assertEquals(transaction.getInputsCount(), transaction.getInputs().length);
                Assertions.assertEquals(transaction.getOutputsCount(), transaction.getOutputs().length);
                Assertions.assertEquals(totalInput, totalOutput + transaction.getFee());
            }
        }
    }
}