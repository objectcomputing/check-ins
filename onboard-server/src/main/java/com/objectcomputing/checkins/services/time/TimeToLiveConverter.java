package com.objectcomputing.checkins.services.time;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class TimeToLiveConverter implements AttributeConverter<TimeToLive, Long> {
    @Override
    public Long convertToDatabaseColumn(TimeToLive attribute) {
        return null == attribute ? null : attribute.getTime();
    }

    @Override
    public TimeToLive convertToEntityAttribute(Long persistedValue) {
        return null == persistedValue ? null : new TimeToLive(persistedValue);
    }
}
