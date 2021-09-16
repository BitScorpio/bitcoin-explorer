package com.scorpius.explorer.bitcoin.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.scorpius.explorer.RateLimitAvoider;
import com.scorpius.explorer.bitcoin.RateLimitedBTCExplorer;
import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCInput;
import com.scorpius.explorer.bitcoin.record.BTCOutput;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;
import dev.yasper.rump.Rump;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * An API implementation of the <a href="https://www.blockchain.com/api">Blockchain Explorer API</a>.
 */
public class BlockchainBTCExplorer extends RateLimitedBTCExplorer {

    private static final int MAX_TXS_PER_CALL = 50;
    private static final String API_BASE = "https://blockchain.info/";
    private static final String API_ADDRESS = API_BASE + "rawaddr/";
    private static final String API_TRANSACTION = API_BASE + "rawtx/";

    public BlockchainBTCExplorer() {
        super(Duration.ofSeconds(5));
    }

    public BlockchainBTCExplorer(Duration timeBetweenCalls) {
        super(timeBetweenCalls);
    }

    public BlockchainBTCExplorer(RateLimitAvoider rateLimitAvoider) {
        super(rateLimitAvoider);
    }

    @Override
    protected BTCAddress getAddressLatestTransactions(String address) throws Throwable {
        Callable<BTCAddress> callable = () -> Rump.get(API_ADDRESS + address + "?limit=" + MAX_TXS_PER_CALL, BTCAddress.class, requestConfig).getBody();
        return rateLimitAvoider == null ? callable.call() : rateLimitAvoider.process(callable);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected BTCAddress getAddressNextTransactionsBatch(BTCAddress existingAddress) throws Throwable {
        int offset = existingAddress == null ? 0 : existingAddress.transactions().size();
        Callable<BTCAddress> callable = () -> Rump.get(API_ADDRESS + existingAddress.hash() + "?limit=" + MAX_TXS_PER_CALL + "&offset=" + offset, BTCAddress.class, requestConfig).getBody();
        BTCAddress newAddress = rateLimitAvoider == null ? callable.call() : rateLimitAvoider.process(callable);
        if (existingAddress == null) {
            return newAddress;
        }
        existingAddress.combineTransactions(newAddress);
        return existingAddress;
    }

    @Override
    public BTCTransaction getTransaction(String hash) throws Throwable {
        Callable<BTCTransaction> callable = () -> Rump.get(API_TRANSACTION + hash, BTCTransaction.class, requestConfig).getBody();
        if (rateLimitAvoider == null) {
            return callable.call();
        }
        return rateLimitAvoider.process(callable);
    }

    @Override
    protected JsonDeserializer<BTCAddress> createAddressDeserializer() {
        return new JsonDeserializer<>() {
            @Override
            public BTCAddress deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
                String hash = null;
                long balance = -1;
                long transactionsCount = -1;
                List<BTCTransaction> transactions = null;

                while (!parser.isClosed()) {
                    JsonToken token = parser.nextToken();
                    if (token == JsonToken.FIELD_NAME) {
                        String fieldName = parser.getCurrentName();
                        parser.nextToken();
                        switch (fieldName) {
                            case "address" -> hash = parser.getValueAsString();
                            case "final_balance" -> balance = parser.getValueAsLong();
                            case "n_tx" -> transactionsCount = parser.getValueAsLong();
                            case "txs" -> transactions = parser.readValueAs(new TypeReference<List<BTCTransaction>>() {});
                        }
                    }
                }

                return new BTCAddress(hash, balance, transactionsCount, transactions);
            }
        };
    }

    @Override
    protected JsonDeserializer<BTCTransaction> createTransactionDeserializer() {
        return new JsonDeserializer<>() {
            @Override
            public BTCTransaction deserialize(JsonParser parser, DeserializationContext ctx) throws IOException {
                JsonNode rootNode = parser.getCodec().readTree(parser);

                String hash = rootNode.get("hash").asText();
                long blockHeight = rootNode.get("block_height").asLong();
                long fee = rootNode.get("fee").asLong();
                int inputsCount = rootNode.get("vin_sz").asInt();
                int outputsCount = rootNode.get("vout_sz").asInt();

                JsonNode inputsNode = rootNode.get("inputs");
                List<BTCInput> inputs = new ArrayList<>();

                for (int i = 0; i < inputsNode.size(); i++) {
                    JsonNode inputNode = inputsNode.get(i).get("prev_out");
                    String address = inputNode.get("addr").asText();
                    long satoshis = inputNode.get("value").asLong();
                    inputs.add(new BTCInput(address, satoshis));
                }

                JsonNode outputsNode = rootNode.get("out");
                List<BTCOutput> outputs = new ArrayList<>();

                for (int i = 0; i < outputsNode.size(); i++) {
                    JsonNode outputNode = outputsNode.get(i);
                    String address = outputNode.get("addr").asText();
                    long satoshis = outputNode.get("value").asLong();
                    outputs.add(new BTCOutput(address, satoshis));
                }

                return new BTCTransaction(hash, blockHeight, fee, inputsCount, outputsCount, inputs, outputs);
            }
        };
    }
}
