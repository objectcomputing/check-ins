package com.objectcomputing.checkins.services.settings;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class SettingOptionDeserializer extends StdDeserializer<SettingOption> {

    public SettingOptionDeserializer() {
        this(null);
    }

    public SettingOptionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SettingOption deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode node = mapper.readTree(parser);
        String name = node.get("name").asText();
        return SettingOption.valueOf(name);
    }
}