package com.objectcomputing.checkins.services.guild.member;

public class GuildMemberBadArgException extends RuntimeException {
    public GuildMemberBadArgException(String message) {
        super(message);
    }
}