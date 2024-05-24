package com.objectcomputing.checkins.services.settings;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class SettingOptionSerializer extends StdSerializer<SettingOption> {

    public SettingOptionSerializer() {
        super(SettingOption.class);
    }

    public SettingOptionSerializer(Class t) {
        super(t);
    }

    public void serialize(
            SettingOption settingOption, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("name");
        generator.writeString(settingOption.name());
        generator.writeFieldName("description");
        generator.writeString(settingOption.getDescription());
        generator.writeFieldName("category");
        generator.writeString(settingOption.getCategory().name());
        generator.writeFieldName("type");
        generator.writeString(settingOption.getType().name());
        generator.writeEndObject();
    }
}