package com.scorpius.explorer.bitcoin;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.scorpius.explorer.bitcoin.impl.BlockchainBTCExplorer;
import com.scorpius.explorer.bitcoin.impl.BlockcypherBTCExplorer;
import com.scorpius.explorer.bitcoin.impl.MultiBTCExplorer;
import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCInput;
import com.scorpius.explorer.bitcoin.record.BTCOutput;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;
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
                    Assertions.assertEquals(rawAddress, address.hash());
                    Assertions.assertNotEquals(0, address.transactionsCount());
                    Assertions.assertNotEquals(0, address.transactions().size());
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
                          address.hash(),
                          address.transactions().size(),
                          address.transactionsCount());

                long totalSent = address.transactions()
                                        .stream()
                                        .filter(tx -> tx.inputs().stream().anyMatch(in -> in.address().equals(address.hash())))
                                        .flatMap(tx -> tx.inputs().stream())
                                        .filter(in -> in.address().equals(address.hash()))
                                        .mapToLong(BTCInput::satoshis)
                                        .sum();

                long totalReceived = address.transactions()
                                            .stream()
                                            .filter(tx -> tx.outputs().stream().anyMatch(out -> out.address().equals(address.hash())))
                                            .flatMap(tx -> tx.outputs().stream())
                                            .filter(out -> out.address().equals(address.hash()))
                                            .mapToLong(BTCOutput::satoshis)
                                            .sum();

                // Test if balance matches sent/received transactions
                Assertions.assertEquals(address.balance(), totalReceived - totalSent);
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
                    Assertions.assertEquals(rawTransaction, transaction.hash());
                    Assertions.assertNotEquals(0, transaction.fee());
                    Assertions.assertNotEquals(0, transaction.blockHeight());
                    Assertions.assertNotEquals(0, transaction.inputsCount());
                    Assertions.assertNotEquals(0, transaction.outputsCount());
                    Assertions.assertNotEquals(0, transaction.inputs().size());
                    Assertions.assertNotEquals(0, transaction.outputs().size());
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
                log.debug("[{}] [{}] Checking", transaction.getClass().getSimpleName(), transaction.hash());

                long totalInput = transaction.inputs()
                                             .stream()
                                             .mapToLong(BTCInput::satoshis)
                                             .sum();

                long totalOutput = transaction.outputs()
                                              .stream()
                                              .mapToLong(BTCOutput::satoshis)
                                              .sum();

                Assertions.assertEquals(transaction.inputsCount(), transaction.inputs().size());
                Assertions.assertEquals(transaction.outputsCount(), transaction.outputs().size());
                Assertions.assertEquals(totalInput, totalOutput + transaction.fee());
            }
        }
    }
}