package com.objectcomputing.checkins.services.commons.accessor;

import java.util.Objects;
import java.util.UUID;

public class Accessor {
    private UUID id;
    private AccessorSource source;

    public Accessor(UUID id, AccessorSource source) {
        this.id = id;
        this.source = source;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AccessorSource getSource() {
        return source;
    }

    public void setSource(AccessorSource source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accessor accessor = (Accessor) o;
        return Objects.equals(id, accessor.id) && source == accessor.source;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, source);
    }

    @Override
    public String toString() {
        return "Accessor{" +
                "id=" + id +
                ", source=" + source +
                '}';
    }
}
