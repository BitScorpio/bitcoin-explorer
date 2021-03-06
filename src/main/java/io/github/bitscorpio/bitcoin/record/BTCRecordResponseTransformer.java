package io.github.bitscorpio.bitcoin.record;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.yasper.rump.response.ResponseTransformer;
import java.io.IOException;
import java.io.InputStream;

/**
 * Used by <a href="https://github.com/Jasper-ketelaar/Rump">Rump</a> to deserialize JSON objects.
 */
public class BTCRecordResponseTransformer implements ResponseTransformer {

    private final ObjectMapper objectMapper;

    /**
     * A constructor that uses the supplied JSON deserializers to return {@link BTCAddress} and {@link BTCTransaction} records.
     * @param addressDeserializer {@link BTCAddress} JSON deserializer.
     * @param transactionDeserializer {@link BTCTransaction} JSON deserializer.
     */
    public BTCRecordResponseTransformer(JsonDeserializer<BTCAddress> addressDeserializer, JsonDeserializer<BTCTransaction> transactionDeserializer) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BTCAddress.class, addressDeserializer);
        module.addDeserializer(BTCTransaction.class, transactionDeserializer);

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(module);
    }

    @Override
    public <T> T transform(InputStream from, Class<T> toType) throws IOException {
        return objectMapper.readValue(from, toType);
    }
}
