package com.objectcomputing.checkins.services.guilds;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuildDeserializer extends StdDeserializer<Guild> {

    private static final Logger LOG = LoggerFactory.getLogger(GuildDeserializer.class);

    public GuildDeserializer() {
        this(null);
    }

    public GuildDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Guild deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        UUID guildId = null;
        JsonNode idNode = node.get("guildId");
        if(idNode != null) {
            guildId = UUID.fromString(idNode.asText());
        }

        String name = null;
        JsonNode nameNode = node.get("name");
        if(nameNode != null) {
            name = nameNode.asText();
        }

        String description = null;
        JsonNode descriptionNode = node.get("description");
        if(descriptionNode != null) {
            description = descriptionNode.asText();
        }

        JsonNode membersNode = node.get("members");
        ArrayList<GuildMember> members = new ArrayList<>();
        if(membersNode != null) {
            TypeReference<HashMap<String, Boolean>> typeRef
                    = new TypeReference<HashMap<String, Boolean>>() {};
            Map<String, Boolean> tmpMap = new ObjectMapper().readValue(membersNode.toString(), typeRef);
            for(Map.Entry<String, Boolean> entry : tmpMap.entrySet()) {
                try {
                    UUID uuid = UUID.fromString(entry.getKey());
                    members.add(new GuildMember(guildId, uuid, entry.getValue()));
                } catch (IllegalArgumentException e) {
                    LOG.warn(String.format("GuildDeserilization: Skipping Member: %s which is an invalid UUID", entry.getKey()));
                }
            }
        }

        return new Guild(guildId, name, description, members);
    }
}
