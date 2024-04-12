package com.objectcomputing.checkins.services.permissions;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class PermissionSerializer extends StdSerializer<Permission> {

    public PermissionSerializer() {
        super(Permission.class);
    }

    public PermissionSerializer(Class t) {
        super(t);
    }

    public void serialize(
      Permission permission, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
        generator.writeStartObject();
        generator.writeFieldName("permission");
        generator.writeString(permission.name());
        generator.writeFieldName("description");
        generator.writeString(permission.getDescription());
        generator.writeFieldName("category");
        generator.writeString(permission.getCategory());
        generator.writeEndObject();
    }
}