package com.example.feedct;

import com.example.feedct.pojos.User;

import ir.mirrajabi.searchdialog.core.Searchable;

public class SearchableUser implements Searchable, Comparable<SearchableUser> {
    private String value;
    private User user;

    public SearchableUser(String value, User user) {
        this.value = value;
        this.user = user;
    }

    @Override
    public String getTitle() {
        return value;
    }

    public User getUser() {
        return user;
    }

    public SearchableUser setTitle(String value) {
        this.value = value;
        return this;
    }

    @Override
    public int compareTo(SearchableUser o) {
        return value.compareTo(o.getTitle());
    }
}
