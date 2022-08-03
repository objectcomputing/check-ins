package com.objectcomputing.checkins.security.authentication.token.text;

import com.objectcomputing.checkins.security.authentication.token.time.TimeToLive;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;

public abstract class TimeToLiveFormat extends Format {
    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof TimeToLive)
            return format( (TimeToLive)obj, toAppendTo, pos );
        else if (obj instanceof Number)
            return format( new TimeToLive(((Number)obj).longValue()),
                    toAppendTo, pos );
        else
            throw new IllegalArgumentException("Cannot format given Object as a TimeToLive");
    }

    /**
     * Parses text from the beginning of the given string to produce a date.
     * The method may not use the entire text of the given string.
     * <p>
     * See the {@link #parse(String, ParsePosition)} method for more information
     * on date parsing.
     *
     * @param source A {@code String} whose beginning should be parsed.
     * @return A {@code TimeToLive} parsed from the string.
     * @throws ParseException if the beginning of the specified string
     *            cannot be parsed.
     */
    public TimeToLive parse(String source) throws ParseException {
        ParsePosition pos = new ParsePosition(0);
        TimeToLive result = parse(source, pos);
        if (pos.getIndex() == 0)
            throw new ParseException("Unparseable date: \"" + source + "\"" ,
                    pos.getErrorIndex());
        return result;
    }


    /**
     * Parse a ttl string according to the given parse position.  For
     * example, a time text {@code "1h"} will be parsed into a {@code TimeToLive}
     * that is equivalent to {@code TimeToLive(3600000l}.
     *
     * <p> By default, parsing is lenient: If the input is not in the form used
     * by this object's format method but can still be parsed as a ttl, then
     * the parse succeeds.
     *
     * @param source  The date/time string to be parsed
     *
     * @param pos   On input, the position at which to start parsing; on
     *              output, the position at which parsing terminated, or the
     *              start position if the parse failed.
     *
     * @return      A {@code TimeToLive}, or {@code null} if the input could not be parsed
     */
    public abstract TimeToLive parse(String source, ParsePosition pos);

    /**
     * Parses text from a string to produce a {@code TimeToLive}.
     * <p>
     * The method attempts to parse text starting at the index given by
     * {@code pos}.
     * If parsing succeeds, then the index of {@code pos} is updated
     * to the index after the last character used (parsing does not necessarily
     * use all characters up to the end of the string), and the parsed
     * date is returned. The updated {@code pos} can be used to
     * indicate the starting point for the next call to this method.
     * If an error occurs, then the index of {@code pos} is not
     * changed, the error index of {@code pos} is set to the index of
     * the character where the error occurred, and null is returned.
     * <p>
     * See the {@link #parse(String, ParsePosition)} method for more information
     * on date parsing.
     *
     * @param source A {@code String}, part of which should be parsed.
     * @param pos A {@code ParsePosition} object with index and error
     *            index information as described above.
     * @return A {@code TieToLive} parsed from the string. In case of
     *         error, returns null.
     * @throws NullPointerException if {@code source} or {@code pos} is null.
     */
    public Object parseObject(String source, ParsePosition pos) {
        return parse(source, pos);
    }


}
