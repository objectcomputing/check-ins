package com.objectcomputing.checkins.services.guild.member;

public class GuildMemberNotFoundException extends RuntimeException {
    public GuildMemberNotFoundException(String message) { super(message); }
}