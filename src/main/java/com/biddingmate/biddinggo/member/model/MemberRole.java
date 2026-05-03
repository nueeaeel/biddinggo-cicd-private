package com.biddingmate.biddinggo.member.model;

public enum MemberRole {
    USER, ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
}