package com.scorpius.explorer.bitcoin.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.scorpius.explorer.bitcoin.RateLimitedBTCExplorer;
import com.scorpius.explorer.bitcoin.deserializer.BTCAddressDeserializer;
import com.scorpius.explorer.bitcoin.deserializer.BTCTransactionDeserializer;
import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCInput;
import com.scorpius.explorer.bitcoin.record.BTCOutput;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;
import dev.yasper.rump.Rump;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BlockcypherBTCExplorer extends RateLimitedBTCExplorer {

    public static final int MAX_TXS_PER_CALL = 50;
    private static final String API_BASE = "https://api.blockcypher.com/v1/btc/main/";
    private static final String API_ADDRESS = API_BASE + "addrs/";
    private static final String API_TRANSACTION = API_BASE + "txs/";

    public BlockcypherBTCExplorer() {
        super(Duration.ofSeconds(18), new AddressDeserializer(), new TransactionDeserializer());
    }

    @Override
    public BTCAddress getAddressCombineTransactions(String address, BTCAddress existingAddress) throws Exception {
        StringBuilder beforeParamBuilder = new StringBuilder();
        if (existingAddress != null && existingAddress.transactions().size() > 0) {
            List<BTCTransaction> transactions = existingAddress.transactions();
            beforeParamBuilder.append("&before=").append(transactions.get(transactions.size() - 1).blockHeight());
        }
        Callable<BTCAddress> callable = () -> Rump.get(API_ADDRESS + address + "/full?limit=" + MAX_TXS_PER_CALL + "&txlimit=1000000" + beforeParamBuilder, BTCAddress.class, requestConfig).getBody();
        BTCAddress newAddress = rateLimitAvoider == null ? callable.call() : rateLimitAvoider.process(callable);
        if (existingAddress == null) {
            return newAddress;
        }
        existingAddress.combineTransactions(newAddress);
        return existingAddress;
    }

    @Override
    public BTCTransaction getTransaction(String hash) throws Exception {
        Callable<BTCTransaction> callable = () -> Rump.get(API_TRANSACTION + hash + "?limit=1000000", BTCTransaction.class, requestConfig).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }

    private static class AddressDeserializer extends BTCAddressDeserializer {

        protected AddressDeserializer() {
            super(new TransactionDeserializer());
        }

        @Override
        public BTCAddress deserialize(JsonNode rootNode) {
            String hash = rootNode.get("address").asText();
            long balance = rootNode.get("final_balance").asLong();
            long transactionsCount = rootNode.get("final_n_tx").asLong();

            @SuppressWarnings("DuplicatedCode")
            JsonNode transactionsNode = rootNode.get("txs");
            List<BTCTransaction> transactions = new ArrayList<>();
            for (JsonNode transactionNode : transactionsNode) {
                transactions.add(transactionDeserializer.deserialize(transactionNode));
            }

            return new BTCAddress(hash, balance, transactionsCount, transactions);
        }
    }

    private static class TransactionDeserializer extends BTCTransactionDeserializer {

        @Override
        public BTCTransaction deserialize(JsonNode rootNode) {
            String hash = rootNode.get("hash").asText();
            long blockHeight = rootNode.get("block_height").asLong();
            long fee = rootNode.get("fees").asLong();
            int inputsCount = rootNode.get("vin_sz").asInt();
            int outputsCount = rootNode.get("vout_sz").asInt();

            JsonNode inputsNode = rootNode.get("inputs");
            List<BTCInput> inputs = new ArrayList<>();

            for (int i = 0; i < inputsNode.size(); i++) {
                JsonNode inputNode = inputsNode.get(i);
                String address = inputNode.get("addresses").get(0).asText();
                long satoshis = inputNode.get("output_value").asLong();
                inputs.add(new BTCInput(address, satoshis));
            }

            JsonNode outputsNode = rootNode.get("outputs");
            List<BTCOutput> outputs = new ArrayList<>();

            for (int i = 0; i < outputsNode.size(); i++) {
                JsonNode outputNode = outputsNode.get(i);
                String address = outputNode.get("addresses").get(0).asText();
                long satoshis = outputNode.get("value").asLong();
                outputs.add(new BTCOutput(address, satoshis));
            }

            return new BTCTransaction(hash, blockHeight, fee, inputsCount, outputsCount, inputs, outputs);
        }
    }
}
