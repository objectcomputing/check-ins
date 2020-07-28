package com.objectcomputing.checkins.services.guilds;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class GuildSerializer extends StdSerializer<Guild> {
    public GuildSerializer() {
        this(null);
    }

    public GuildSerializer(Class<Guild> vc) {
        super(vc);
    }

    @Override
    public void serialize(Guild value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("guildId", value.getGuildId().toString());
        gen.writeStringField("name", value.getName());
        gen.writeStringField("description", value.getDescription());
        gen.writeArrayFieldStart("members");
        for(GuildMember gm : value.getMembers()) {
            gen.writeBooleanField(gm.getGuildId().toString(), gm.isLead());
        }
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
