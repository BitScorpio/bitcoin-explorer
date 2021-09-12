package com.scorpius.blockchain.bitcoin;

import java.util.Arrays;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class BTCExplorerTest {

    private final String[] txHashes;
    private final String[] addresses;
    private final BTCExplorer explorer;

    public BTCExplorerTest() {
        this.explorer = new BTCExplorer();

        this.txHashes = new String[]{"5b361631bd5c47a5476cd3a1f216ab8c6219421c2d3c660fe23041b309e655a7",
                                     "38d97c6bfecdbe201e17787534674bccb57f292a19a99f9f421ebbf6347fbd45",
                                     "38d97c6bfecdbe201e17787534674bccb57f292a19a99f9f421ebbf6347fbd45",
                                     "5b361631bd5c47a5476cd3a1f216ab8c6219421c2d3c660fe23041b309e655a7"};

        this.addresses = new String[]{"1NWjoHZavQ2Fud4MAoHkDuYFDVGof4u8pr",
                                      "3Dxq8WVEF5CHUcNVPmBVFdTKUhoiafMVwg",
                                      "12RUqdJfKR44q6fJd3RbbfSfST84tvSCAV",
                                      "3D7VugNn3u8kMZXj1dSYvwHUsy92b85d94"};

    }

    @Test
    @SneakyThrows
    void getAddress() {
        for (String addr : addresses) {
            log.debug("Testing address: " + addr);
            BTCAddress address = explorer.getAddress(addr);

            long totalSent = Arrays.stream(address.getTransactions())
                                   .filter(tx -> Arrays.stream(tx.getInputs()).anyMatch(in -> in.getAddress().equals(address.getHash())))
                                   .flatMap(tx -> Arrays.stream(tx.getInputs()))
                                   .filter(in -> in.getAddress().equals(address.getHash()))
                                   .mapToLong(BTCInput::getSatoshis)
                                   .sum();

            long totalReceived = Arrays.stream(address.getTransactions())
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

    @Test
    @SneakyThrows
    void getTransaction() {
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