package com.scorpius.explorer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public abstract class SimpleJsonDeserializer<T> extends JsonDeserializer<T> {

    public abstract T deserialize(JsonNode rootNode);

    @Override
    public final T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        return deserialize(jsonParser.getCodec().readTree(jsonParser));
    }
}
