package com.objectcomputing.checkins.security.authentication.token.text;

import com.objectcomputing.checkins.security.authentication.token.time.TimeToLive;

import java.text.ParsePosition;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleTimeToLiveFormat extends TimeToLiveFormat {
    private final long SECOND_MILLS = 1000L;
    private final long MINUTE_MILLS = SECOND_MILLS * 60L;
    private final long HOUR_MILLS   = MINUTE_MILLS * 60L;

    private final SimpleTimeToLivePatternConverter[] ttlPatternConverters = {
            new SimpleTimeToLivePatternConverter("Seconds", Pattern.compile("(\\d+)s|(\\d+)S"), (count) -> count * SECOND_MILLS),
            new SimpleTimeToLivePatternConverter("Minutes", Pattern.compile("(\\d+)m|(\\d+)M"), (count) -> count * MINUTE_MILLS),
            new SimpleTimeToLivePatternConverter("Hours", Pattern.compile("(\\d+)h|(\\d+)H"), (count) -> count * HOUR_MILLS),
    };

    public SimpleTimeToLiveFormat() {
    }

    @Override
    public TimeToLive parse(String source, ParsePosition pos) {
        int index = pos.getIndex();
        int errorIndex = -1;

        Long time = null;

        for(SimpleTimeToLivePatternConverter converter : ttlPatternConverters) {
            Matcher matcher = converter.getPattern().matcher(source);
            if(matcher.find(index)) {

                try {
                    time = converter.calculate(Long.parseLong(matcher.group(1)));
                    index++;
                } catch(NumberFormatException error) {
                    errorIndex = index;
                }

                break;
            }
        }

        pos.setIndex(index);
        pos.setErrorIndex(errorIndex);

        return time == null ? null : new TimeToLive(time);
    }

    public static final class SimpleTimeToLivePatternConverter {
        private final String name;
        private final Pattern pattern;
        private final TimeToLiveConverter converter;

        SimpleTimeToLivePatternConverter(String name, Pattern pattern, TimeToLiveConverter converter) {
            this.name = name;
            this.pattern = pattern;
            this.converter = converter;
        }

        public String getName() {
            return name;
        }

        public Pattern getPattern() {
            return this.pattern;
        }

        public TimeToLiveConverter getConverter() {
            return this.converter;
        }

        public long calculate(long count) {
            return getConverter().calculate(count);
        }
    }

    private interface TimeToLiveConverter {
        long calculate(long count);
    }
}
