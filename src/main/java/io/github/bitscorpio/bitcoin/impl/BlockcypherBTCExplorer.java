package io.github.bitscorpio.bitcoin.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.yasper.rump.Rump;
import io.github.bitscorpio.RateLimitAvoider;
import io.github.bitscorpio.bitcoin.RateLimitedBTCExplorer;
import io.github.bitscorpio.bitcoin.record.BTCAddress;
import io.github.bitscorpio.bitcoin.record.BTCInput;
import io.github.bitscorpio.bitcoin.record.BTCOutput;
import io.github.bitscorpio.bitcoin.record.BTCTransaction;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * An API implementation of the <a href="https://www.blockcypher.com/dev/bitcoin/">Blockciper API</a>.
 */
public class BlockcypherBTCExplorer extends RateLimitedBTCExplorer {

    public static final int MAX_TXS_PER_CALL = 50;
    private static final String API_BASE = "https://api.blockcypher.com/v1/btc/main/";
    private static final String API_ADDRESS = API_BASE + "addrs/";
    private static final String API_TRANSACTION = API_BASE + "txs/";

    public BlockcypherBTCExplorer() {
        super(Duration.ofSeconds(18));
    }

    public BlockcypherBTCExplorer(Duration timeBetweenCalls) {
        super(timeBetweenCalls);
    }

    public BlockcypherBTCExplorer(RateLimitAvoider rateLimitAvoider) {
        super(rateLimitAvoider);
    }

    @Override
    protected BTCAddress getAddressLatestTransactions(String address) throws Throwable {
        Callable<BTCAddress> callable = () -> Rump.get(API_ADDRESS + address + "/full?limit=" + MAX_TXS_PER_CALL + "&txlimit=1000000", BTCAddress.class, requestConfig).getBody();
        return rateLimitAvoider == null ? callable.call() : rateLimitAvoider.process(callable);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected BTCAddress getAddressNextTransactionsBatch(BTCAddress existingAddress) throws Throwable {
        StringBuilder beforeParamBuilder = new StringBuilder();
        if (existingAddress != null && !existingAddress.transactions().isEmpty()) {
            List<BTCTransaction> transactions = existingAddress.transactions();
            beforeParamBuilder.append("&before=").append(transactions.get(transactions.size() - 1).blockHeight());
        }
        Callable<BTCAddress> callable = () -> Rump.get(API_ADDRESS + existingAddress.hash() + "/full?limit=" + MAX_TXS_PER_CALL + "&txlimit=1000000" + beforeParamBuilder, BTCAddress.class, requestConfig).getBody();
        BTCAddress newAddress = rateLimitAvoider == null ? callable.call() : rateLimitAvoider.process(callable);
        if (existingAddress == null) {
            return newAddress;
        }
        existingAddress.combineTransactions(newAddress);
        return existingAddress;
    }

    @Override
    public BTCTransaction getTransaction(String hash) throws Throwable {
        Callable<BTCTransaction> callable = () -> Rump.get(API_TRANSACTION + hash + "?limit=1000000", BTCTransaction.class, requestConfig).getBody();
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
                            case "final_n_tx" -> transactionsCount = parser.getValueAsLong();
                            case "txs" -> transactions = parser.readValueAs(new TypeReference<List<BTCTransaction>>() {
                            });
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
            public BTCTransaction deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
                JsonNode rootNode = jp.getCodec().readTree(jp);

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
        };
    }
}
