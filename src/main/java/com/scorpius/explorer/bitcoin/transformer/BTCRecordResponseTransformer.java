package com.scorpius.explorer.bitcoin.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.scorpius.explorer.bitcoin.deserializer.BTCAddressDeserializer;
import com.scorpius.explorer.bitcoin.deserializer.BTCTransactionDeserializer;
import com.scorpius.explorer.bitcoin.record.BTCAddress;
import com.scorpius.explorer.bitcoin.record.BTCTransaction;
import dev.yasper.rump.response.ResponseTransformer;
import java.io.IOException;
import java.io.InputStream;

public class BTCRecordResponseTransformer implements ResponseTransformer {

    private final ObjectMapper objectMapper;

    public BTCRecordResponseTransformer(BTCAddressDeserializer addressDeserializer, BTCTransactionDeserializer transactionDeserializer) {
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
